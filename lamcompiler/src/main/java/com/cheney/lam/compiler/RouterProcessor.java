package com.cheney.lam.compiler;

import com.cheney.lam.annotation.ExportAction;
import com.cheney.lam.annotation.ExportFragment;
import com.cheney.lam.annotation.Module;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * Created by cheney on 17/3/1.
 */

@AutoService(Processor.class)
public class RouterProcessor extends AbstractProcessor {

    Elements elementsUtils;

    /**
     * Fragment基类的包路径和基类名，以gradle参数传入
     */
    String mArgFragPkg, mArgFragCls;
    /**
     * Fragment基类的完整路径
     */
    String mFragPath;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        elementsUtils = processingEnvironment.getElementUtils();

        // 参数提取
        mArgFragPkg = processingEnvironment.getOptions().get("BFragPkg");
        mArgFragCls = processingEnvironment.getOptions().get("BFragCls");
        mFragPath = mArgFragPkg + "." + mArgFragCls;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(Module.class.getCanonicalName());
        types.add(ExportFragment.class.getCanonicalName());
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_7;
    }


    String getPackageName(Element element) {
        return elementsUtils.getPackageOf(element).getQualifiedName().toString();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> moduleElement = roundEnv.getElementsAnnotatedWith(Module.class);
        if (moduleElement == null || moduleElement.size() <= 0) {
            System.out.println("@ERROR::每个Module有且仅能定义一个IModule，当前模块可能没有或包含多个IModule !!!");
            return false;
        }

        System.out.println("@Step: 1");

        // Fragment基类TypeName
        ClassName fragClass = ClassName.get(mArgFragPkg, mArgFragCls);
        System.out.println("@Step: 1.1");
        // Bundle类的TypeName
        ClassName bundleClass = ClassName.get("android.os", "Bundle");
        System.out.println("@Step: 1.2");
        // IAction类的TypeName
        ClassName actionClass = ClassName.get("com.cheney.lam.sdk", "IAction");
        System.out.println("@Step: 1.3");
        final String actionClsPath = "com.cheney.lam.sdk.IAction";

        System.out.println("@Step: 2");
        // 生成Finder class的包路径，和定义的IModule相同包
        String packageName = null;
        // IModule类的名称
        String moduleClassName = null;
        for (Element element : moduleElement) {
            packageName = getPackageName(element);
            moduleClassName = element.getSimpleName().toString();
            break;
        }

        System.out.println("@Step: 3");
        // 定义输出的fragment表
        FieldSpec fragMapFiled = FieldSpec.builder(HashMap.class, "sExportFragmentMap")
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                .initializer("new HashMap<String, Class<? extends $L.$L>>(10)", mArgFragPkg, mArgFragCls)
                .build();
        FieldSpec actionClzMapField = FieldSpec.builder(HashMap.class, "sActionClzMap")
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                .initializer("new HashMap<String, Class<? extends $L>>()", actionClsPath)
                .build();
        FieldSpec actionMapField = FieldSpec.builder(ConcurrentHashMap.class, "actionMap")
                .addModifiers(Modifier.PRIVATE)
                .initializer("new ConcurrentHashMap<String, IAction>(15)")
                .build();
        System.out.println("@Step: 4");

        // 生成static代码，为各个静态Map赋值
        CodeBlock.Builder staticCodeBuilder = CodeBlock.builder();
        // 先生成导出的Fragment类表
        for (Element element : roundEnv.getElementsAnnotatedWith(ExportFragment.class)) {
            TypeElement te = (TypeElement) element;
            ExportFragment export = te.getAnnotation(ExportFragment.class);
            staticCodeBuilder.addStatement("sExportFragmentMap.put(\"$L\",$L.class)", export.value(), te.getQualifiedName());
        }

        System.out.println("@Step: 5");
        // 再生成导出的Action表
        for (Element element : roundEnv.getElementsAnnotatedWith(ExportAction.class)) {
            TypeElement te = (TypeElement) element;
            ExportAction export = te.getAnnotation(ExportAction.class);
            String value = export.value();
            String[] apis = value.split(",");
            for (String api : apis) {
                String path = api.trim();
                staticCodeBuilder.addStatement("sActionClzMap.put(\"$L\",$L.class)", path, te.getQualifiedName());
            }
        }

        System.out.println("@Step: 6");
        // Finder接口findFragment方法的实现
        MethodSpec findFragment = MethodSpec.methodBuilder("findFragment")
                .addModifiers(Modifier.PUBLIC)
                .returns(fragClass)
                .addAnnotation(Override.class)
                .addParameter(String.class, "path")
                .addParameter(bundleClass, "params")
                .addStatement("Object obj = sExportFragmentMap.get($L)", "path")
                .beginControlFlow("if (obj != null)")
                .beginControlFlow("try")
                .addStatement("Class<? extends $L> clazz = (Class<? extends $L>)obj", mFragPath, mFragPath)
                .addStatement(mFragPath + " instance = clazz.newInstance()")
                .addStatement("instance.setArguments(params)")
                .addStatement("return instance")
                .endControlFlow()
                .beginControlFlow("catch(InstantiationException e)")
                .endControlFlow()
                .beginControlFlow("catch(IllegalAccessException e)")
                .endControlFlow()
                .endControlFlow()
                .addStatement("return null")
                .build();
        System.out.println("@Step: 7");
        // findAction接口
        MethodSpec findAction = MethodSpec.methodBuilder("findAction")
                .addModifiers(Modifier.PUBLIC)
                .returns(actionClass)
                .addAnnotation(Override.class)
                .addParameter(String.class, "api")
                .addStatement("android.util.Log.w(\"Finder\", \"findAction : \" +  api);")
                .addStatement("IAction action = (IAction)actionMap.get($L)", "api")
                .beginControlFlow("if (action == null)")
                .addStatement("Object obj = sActionClzMap.get($L)", "api")
                .beginControlFlow("if (obj != null)")
                .beginControlFlow("try")
                .addStatement("Class<? extends $L> clazz = (Class<? extends $L>)obj", actionClsPath, actionClsPath)
                .addStatement(actionClsPath + " instance = clazz.newInstance()")
                .addStatement("actionMap.put($L,instance)", "api")
                .addStatement("return instance")
                .endControlFlow()
                .beginControlFlow("catch(InstantiationException e)")
                .addStatement("android.util.Log.w(\"Finder\", \"findAction InstantiationException\");")
                .endControlFlow()
                .beginControlFlow("catch(IllegalAccessException e)")
                .addStatement("android.util.Log.w(\"Finder\", \"findAction IllegalAccessException\");")
                .endControlFlow()
                .endControlFlow()
                .beginControlFlow("else")
                .addStatement("android.util.Log.w(\"Finder\", \"findAction clazz not found, size: \" +  sActionClzMap.size());")
                .endControlFlow()
                .endControlFlow()
                .addStatement("return action")
                .build();

        System.out.println("@Step: 8");
        // Finder类
        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(moduleClassName + "$$Finder")
                .addModifiers(Modifier.FINAL, Modifier.PUBLIC)
                .addSuperinterface(ClassName.get("com.cheney.lam.sdk.finder", "IFinder"))
                .addField(fragMapFiled)
                .addField(actionClzMapField)
                .addField(actionMapField)
                .addMethod(findFragment)
                .addMethod(findAction)
                .addStaticBlock(staticCodeBuilder.build());

        // 生成文件
        JavaFile javaFile = JavaFile.builder(packageName, classBuilder.build())
                .build();
        try {
            System.out.println("@Step: 9");
            javaFile.writeTo(processingEnv.getFiler());
            System.out.println("@Step: 10");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
