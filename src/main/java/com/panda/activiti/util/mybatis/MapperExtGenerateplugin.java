package com.panda.activiti.util.mybatis;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.XmlConstants;
import org.mybatis.generator.config.PropertyRegistry;

public class MapperExtGenerateplugin extends PluginAdapter {

    private static String XMLFILE_SUFFIX  = "Ext";

    private static String JAVAFILE_SUFFIX = "Ext";

    private static String ANNOTATION_TYPE = "org.springframework.stereotype.Repository";

    private static String ANNOTATION_NAME = "@Repository";

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {

        XmlElement parentElement = document.getRootElement();

        _updateDocumentNameSpace(introspectedTable, parentElement);

        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }

    private void _updateDocumentNameSpace(IntrospectedTable introspectedTable, XmlElement parentElement) {
        Attribute namespaceAttribute = null;
        for (Attribute attribute : parentElement.getAttributes()) {
            if (attribute.getName().equals("namespace")) {
                namespaceAttribute = attribute;
            }
        }
        parentElement.getAttributes().remove(namespaceAttribute);
        parentElement.getAttributes().add(new Attribute("namespace", introspectedTable.getMyBatis3JavaMapperType()
                                                                     + JAVAFILE_SUFFIX));
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
        FullyQualifiedJavaType type = new FullyQualifiedJavaType(introspectedTable.getMyBatis3JavaMapperType()
                                                                 + JAVAFILE_SUFFIX);
        Interface _interface = new Interface(type);
        _interface.setVisibility(JavaVisibility.PUBLIC);
        context.getCommentGenerator().addJavaFileComment(_interface);

        FullyQualifiedJavaType base_interface = new FullyQualifiedJavaType(
                                                                           introspectedTable.getMyBatis3JavaMapperType());
        _interface.addSuperInterface(base_interface);

        FullyQualifiedJavaType annotation = new FullyQualifiedJavaType(ANNOTATION_TYPE);
        _interface.addAnnotation(ANNOTATION_NAME);
        _interface.addImportedType(annotation);

        CompilationUnit compilationUnits = _interface;
        GeneratedJavaFile generatedJavaFile = new GeneratedJavaFile(
                                                                    compilationUnits,
                                                                    context.getJavaModelGeneratorConfiguration().getTargetProject(),
                                                                    context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING),
                                                                    context.getJavaFormatter());

        if (isExistExtFile(generatedJavaFile.getTargetProject(), generatedJavaFile.getTargetPackage(),
                           generatedJavaFile.getFileName())) {
            return super.contextGenerateAdditionalJavaFiles(introspectedTable);
        }
        List<GeneratedJavaFile> generatedJavaFiles = new ArrayList<GeneratedJavaFile>(1);
        generatedJavaFile.getFileName();
        generatedJavaFiles.add(generatedJavaFile);
        return generatedJavaFiles;
    }

    @Override
    public List<GeneratedXmlFile> contextGenerateAdditionalXmlFiles(IntrospectedTable introspectedTable) {
        String[] splitFile = introspectedTable.getMyBatis3XmlMapperFileName().split("\\.");
        String fileNameExt = null;
        if (splitFile[0] != null) {
            fileNameExt = splitFile[0] + XMLFILE_SUFFIX + ".xml";
        }

        if (isExistExtFile(context.getSqlMapGeneratorConfiguration().getTargetProject(),
                           introspectedTable.getMyBatis3XmlMapperPackage(), fileNameExt)) {
            return super.contextGenerateAdditionalXmlFiles(introspectedTable);
        }

        Document document = new Document(XmlConstants.MYBATIS3_MAPPER_PUBLIC_ID, XmlConstants.MYBATIS3_MAPPER_SYSTEM_ID);

        XmlElement root = new XmlElement("mapper");
        document.setRootElement(root);
        String namespace = introspectedTable.getMyBatis3SqlMapNamespace() + XMLFILE_SUFFIX;
        root.addAttribute(new Attribute("namespace", namespace));

        GeneratedXmlFile gxf = new GeneratedXmlFile(document, fileNameExt,
                                                    introspectedTable.getMyBatis3XmlMapperPackage(),
                                                    context.getSqlMapGeneratorConfiguration().getTargetProject(),
                                                    false, context.getXmlFormatter());

        List<GeneratedXmlFile> answer = new ArrayList<GeneratedXmlFile>(1);
        answer.add(gxf);

        return answer;
    }

    @Override
    public boolean validate(List<String> arg0) {
        return true;
    }

    private boolean isExistExtFile(String targetProject, String targetPackage, String fileName) {

        File project = new File(targetProject);
        if (!project.isDirectory()) {
            return true;
        }

        StringBuilder sb = new StringBuilder();
        String[] packages = targetPackage.split(".");
        for (String p : packages) {
            sb.append(p);
            sb.append(File.separatorChar);
        }

        File directory = new File(project, sb.toString());
        if (!directory.isDirectory()) {
            boolean rc = directory.mkdirs();
            if (!rc) {
                return true;
            }
        }

        File testFile = new File(directory, fileName);
        if (testFile.exists()) {
            return true;
        } else {
            return false;
        }
    }

}
