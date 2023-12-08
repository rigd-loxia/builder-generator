<#macro generateContent cls indent=1>
    <#list cls.members as member>
        <@generateFields cls member indent/>
    </#list>

    <@generateConstructor cls indent/>

    <#-- for all fields -->
    <#list cls.members as member>
        <@generateWithMethod cls member indent/>

        <#if member.hasBuilder() && !member.hasSubType()>
        <@generateWithBuilderMethod cls member indent/>

        </#if>
        <#if cls.isBuilderPassingConstructor()>
        <@generateGetMethod cls member indent/>

        </#if>
    </#list>
    <#-- for only List<T> collection fields -->
    <#list cls.collectionMembers as member>
        <@generateCollectionAddIterable cls member indent/>

        <@generateCollectionAddVarArgs cls member indent/>

        <#if member.hasBuilder()>
            <#if !member.isAbstract()>
                <@generateCollectionAddBuilder cls member indent/>

            </#if>
            <#list member.aliases as alias>
                <@generateCollectionAddAliasBuilder cls member alias indent/>
            </#list>
        </#if>
    </#list>
    <@generateBuildMethod cls indent/>
    <@generateEndMethod cls indent/>
    <#if cls.isCopyOfGenerationEnabled()>
        <@generateCopyOfMethod cls indent/>
    </#if>
    <@generateInnerClassBuilder cls indent/>
</#macro>

<#-- Inner class builder -->
<#macro generateInnerClassBuilder cls indent>
    <#local spc>${""?left_pad(indent * 4)}</#local>
    <#list cls.innerClasses as innerClass>

${spc}public static class ${innerClass.builderClassName}<PARENT> <#if innerClass.extendsBuilder()>extends ${innerClass.extendedBuilderName}<PARENT> </#if>{
        <@class.generateContent innerClass indent+1/>
${spc}}
    </#list>
</#macro>

<#-- Generation of fields -->
<#macro generateFields cls member indent>
    <#local spc>${""?left_pad(indent * 4)}</#local>
${spc}private <@com.type member.type packageName/> ${member.name}<#if member.hasSubType()> = new java.util.ArrayList<>()</#if>;
    <#if member.hasBuilder()>
    <#if member.hasSubType()>
${spc}private java.util.List<<@com.builderType member packageName sourceClassName/><${cls.builderClassName}<PARENT>>> ${member.name}Builders = new java.util.ArrayList<>();
    <#else>
${spc}private <@com.builderType member packageName sourceClassName/><${cls.builderClassName}<PARENT>> ${member.name}Builder;
    </#if>
    </#if>
</#macro>

<#macro generateConstructor cls indent>
    <#local spc>${""?left_pad(indent * 4)}</#local>
    <#if !cls.extendsBuilder()>
${spc}private PARENT parent;

    </#if>
${spc}public ${cls.builderClassName}() {
${spc}}

${spc}public ${cls.builderClassName}(PARENT parent) {
        <#if cls.extendsBuilder()>
${spc}    super(parent);
        <#else>
${spc}    this.parent = parent;
        </#if>
${spc}}
</#macro>

<#macro generateWithMethod cls member indent>
    <#local spc>${""?left_pad(indent * 4)}</#local>
    <@inherited member indent/>
    <#if member.isList()>
${spc}/**
        <@javadoc member spc true/>
${spc} * This replaces the collection currently present. Any previous calls to add${member.name?cap_first} are not saved.
${spc} */
    <#elseif member.hasBuilder()>
${spc}/**
        <@javadoc member spc true/>
${spc} * If this is called after {@link #${member.methodName}()} then the Builder is lost.
${spc} */
    <#else>
        <@javadoc member spc false/>
    </#if>
${spc}public ${cls.builderClassName}<PARENT> ${member.methodName}(<@com.type member.type packageName/> ${member.name}) {
${spc}    this.${member.name} = ${member.name};
        <#if member.hasBuilder() && !member.hasSubType()>
${spc}    ${member.name}Builder = null;
        <#elseif member.hasBuilder()>
${spc}    ${member.name}Builders = new java.util.ArrayList<>();
        </#if>
${spc}    return this;
${spc}}
</#macro>

<#macro generateGetMethod cls member indent>
    <#local spc>${""?left_pad(indent * 4)}</#local>
    <@inherited member indent/>
${spc}public <@com.type member.type packageName/> get${member.name?cap_first}() {
        <#if member.hasBuilder() && !member.hasSubType()>
${spc}    if (${member.name}Builder != null){
${spc}        return ${member.name}Builder.build();
${spc}    }
        <#elseif member.hasBuilder()>
${spc}    if (!${member.name}Builders.isEmpty()){
${spc}        return ${member.name}Builders.stream().map(builder -> builder.build()).collect(java.util.stream.Collectors.toList());
${spc}    }
${spc}    ${member.name}Builders = new java.util.ArrayList<>();
        </#if>
${spc}    return this.${member.name};
${spc}}
</#macro>

<#macro generateWithBuilderMethod cls member indent>
    <#local spc>${""?left_pad(indent * 4)}</#local>
    <@inherited member indent/>
${spc}/**
    <@javadoc member spc true/>
${spc} * returns a builder for chaining. Use the end() method to return back to the current builder.<BR>
${spc} * Multiple calls to this method will return the same builder.<BR>
${spc} * If this is called after {@link #${member.methodName}(<@com.type member.type packageName/>)} then this will return a new Builder and the previously set object is lost.
${spc} */
${spc}public <@com.builderType member packageName sourceClassName/><? extends ${cls.builderClassName}<PARENT>> ${member.methodName}() {
${spc}    if (${member.name}Builder == null) {
${spc}        ${member.name}Builder = new <@com.builderType member packageName sourceClassName/><>(this);
${spc}        ${member.name} = null;
${spc}    }
${spc}    return ${member.name}Builder;
${spc}}
</#macro>

<#macro generateCollectionAddIterable cls member indent>
    <#local spc>${""?left_pad(indent * 4)}</#local>
    <@inherited member indent/>
${spc}/**
    <@javadoc member spc true/>
    <@javadocAddCollection member spc/>
${spc} */
${spc}public ${cls.builderClassName}<PARENT> add${member.name?cap_first}(Iterable<? extends <@com.type member.subType packageName/>> ${member.name}) {
${spc}    for (<@com.type member.subType packageName/> v : ${member.name}) {
${spc}        this.${member.name}.add(v);
${spc}    }
${spc}    return this;
${spc}}
</#macro>

<#macro generateCollectionAddVarArgs cls member indent>
    <#local spc>${""?left_pad(indent * 4)}</#local>
    <@inherited member indent/>
${spc}/**
    <@javadoc member spc true/>
    <@javadocAddCollection member spc/>
${spc} */
${spc}public ${cls.builderClassName}<PARENT> add${member.name?cap_first}(<@com.type member.subType packageName/>... ${member.name}) {
${spc}    for (<@com.type member.subType packageName/> v : ${member.name}) {
${spc}        this.${member.name}.add(v);
${spc}    }
${spc}    return this;
${spc}}
</#macro>

<#macro generateCollectionAddBuilder cls member indent>
    <#local spc>${""?left_pad(indent * 4)}</#local>
    <@inherited member indent/>
${spc}/**
    <@javadoc member spc true/>
${spc} * Each call to this method creates a new Builder which will be stored in the list. Use the end() method to return back to the current builder.
${spc} */
${spc}public <@com.type member.subBuilderClassName packageName/><? extends ${cls.builderClassName}<PARENT>> add${member.name?cap_first}() {
${spc}    <@com.type member.subBuilderClassName packageName/><${cls.builderClassName}<PARENT>> child = new <@com.type member.subBuilderClassName packageName/><>(this);
${spc}    ${member.name}Builders.add(child);
${spc}    return child;
${spc}}
</#macro>

<#macro generateCollectionAddAliasBuilder cls member alias indent>
    <#local spc>${""?left_pad(indent * 4)}</#local>
    <@inherited member indent/>
    <@javadoc member spc false/>
${spc}public <@com.type alias.type packageName/>Builder<? extends ${cls.builderClassName}<PARENT>> add${alias.name?cap_first}() {
${spc}    <@com.type alias.type packageName/>Builder<${cls.builderClassName}<PARENT>> child = new <@com.type alias.type packageName/>Builder<>(this);
${spc}    ${member.name}Builders.add(child);
${spc}    return child;
${spc}}
</#macro>

<#macro generateBuildMethod cls indent>
    <#local spc>${""?left_pad(indent * 4)}</#local>
${spc}/**
${spc} * returns the build object. For builder chaining use the {@link #end()} method to return the parent builder.
${spc} */
    <#if cls.extendsBuilder()>
${spc}@Override
    </#if>
${spc}public <#if cls.isAbstract()>abstract </#if><@com.type cls.sourceClassName packageName/> build()<#if cls.isAbstract()>;
    <#else> {
        <#if cls.isBuilderPassingConstructor()>
${spc}    return new <@com.type cls.sourceClassName packageName/>(this);
        <#else>
          <@generateBuilderValidation cls spc/>
${spc}    <@com.type cls.sourceClassName packageName/> result = new <@com.type cls.sourceClassName packageName/>(<#list getConstructorMembers() as member><@buildMember member/><#sep>, </#list>);
            <#list cls.settableMembers as member>
                <#if member.hasSetter()>
${spc}    result.set${member.name?cap_first}(<@buildMember member/>);
                <#else>
${spc}    result.${member.name} = <@buildMember member/>;
                </#if>
            </#list>
            <#list cls.collectionMembers as member>
${spc}    result.get${member.name?cap_first}().addAll(${member.name});
                <#if member.hasBuilder()>
${spc}    for (<@com.type member.subBuilderClassName packageName/><?> ${member.name}Builder : ${member.name}Builders) {
${spc}        result.get${member.name?cap_first}().add(${member.name}Builder.build());
${spc}    }
                </#if>
            </#list>
${spc}    return result;
        </#if>
${spc}}
    </#if>
</#macro>

<#macro generateBuilderValidation cls spc>
  <#if cls.getConstructorMembers()?size gt 0 && cls.isBuilderValidationEnabled()>
${spc}    java.util.List<String> missingRequiredFields = new java.util.ArrayList<>();
    <#list cls.getConstructorMembers() as member>
${spc}    if (<@buildMember member/> == null) {
${spc}        missingRequiredFields.add("${member.name}");
${spc}    }
    </#list>
${spc}    if (!missingRequiredFields.isEmpty()) {
${spc}        throw new nl.loxia.builder.generator.annotations.BuilderValidationException("The following required fields are not set: "
${spc}                  + missingRequiredFields.stream().collect(java.util.stream.Collectors.joining(",")));
${spc}    }
  </#if>
</#macro>

<#macro buildMember member>
<#compress>
    <#if member.hasBuilder()>
        ${member.name}Builder != null ? ${member.name}Builder.build() : ${member.name}
    <#else>
        ${member.name}
    </#if>
</#compress>
</#macro>

<#macro inherited member indent>
    <#local spc>${""?left_pad(indent * 4)}</#local>
    <#if member.inherited>
${spc}@Override
    </#if>
</#macro>

<#macro generateEndMethod cls indent>
    <#local spc>${""?left_pad(indent * 4)}</#local>
    <#if !cls.extendsBuilder()>

${spc}/**
${spc} * returns the parent builder if present, otherwise null is returned.
${spc} */
${spc}public PARENT end() {
${spc}    return parent;
${spc}}
    </#if>
</#macro>

<#macro generateCopyOfMethod cls indent>
    <#local spc>${""?left_pad(indent * 4)}</#local>
    <#if !cls.isAbstract()>

${spc}public static ${cls.builderClassName}<Void> copyOf(<@com.type cls.sourceClassName packageName/> bron) {
${spc}    if (bron == null) {
${spc}        return null;
${spc}    }
${spc}    ${cls.builderClassName}<Void> builder = new ${cls.builderClassName}<>();
        <@generateCopyOfBody cls indent/>
${spc}    return builder;
${spc}}

${spc}public static <T> ${cls.builderClassName}<T> copyOf(<@com.type cls.sourceClassName packageName/> bron, T parentBuilder) {
${spc}    if (bron == null) {
${spc}        return null;
${spc}    }
${spc}    ${cls.builderClassName}<T> builder = new ${cls.builderClassName}<>(parentBuilder);
        <@generateCopyOfBody cls indent/>
${spc}    return builder;
${spc}}
    </#if>
</#macro>

<#macro generateCopyOfBody cls indent>
    <#local spc>${""?left_pad(indent * 4)}</#local>
    <#list cls.members as member>
        <#if member.hasBuilder() && !member.hasSubType() && !member.isAbstract()>
${spc}    builder.${member.name}Builder = <@com.builderType member packageName cls.sourceClassName/>.copyOf(bron.get${member.name?cap_first}(), builder);
        <#elseif member.hasBuilder() && !member.isAbstract()>
${spc}    builder.${member.name}Builders = new java.util.ArrayList<>();
${spc}    for (<@com.type member.subType packageName/> original : bron.get${member.name?cap_first}()) {
            <#list member.aliases as alias>
${spc}        if (original instanceof <@com.type alias.type packageName/>){
${spc}            builder.${member.name}Builders.add(<@com.type alias.type packageName/>Builder.copyOf((<@com.type alias.type packageName/>) original, builder));
${spc}        } else
            </#list>
            <#local aliasSpc>${spc}<#if member.hasAliases()>${""?left_pad(4)}</#if></#local>
            <#if member.hasAliases()>
${spc}        {
            </#if>
${aliasSpc}        builder.${member.name}Builders.add(<@com.builderType member packageName cls.sourceClassName/>.copyOf(original, builder));
            <#if member.hasAliases()>
${spc}        }
            </#if>
${spc}    }
        <#elseif member.isList()>
${spc}    builder.${member.name} = new java.util.ArrayList<>(bron.<@getter member/>);
        <#else>
${spc}    builder.${member.name} = bron.<@getter member/>;
        </#if>
    </#list>
</#macro>

<#macro getter member>
<@compress single_line=true>
    <#if member.hasGetter()>
        get${member.name?cap_first}()
    <#else>
        ${member.name}
    </#if>
</@compress>
</#macro>

<#macro javadoc member spc inline>
    <#if member.javadoc??>
        <#if inline>
${spc} * ${member.javadoc?trim}<BR>
${spc} * <BR>
        <#else>
${spc}/**
${spc} * ${member.javadoc?trim}
${spc} */
        </#if>
    </#if>
</#macro>

<#macro javadocAddCollection member spc>
${spc} * this will add the supplied objects to the collection present. If an unmodifiable collection is set using {@link #${member.methodName}(<@com.typeWithoutGenerics member.type packageName/>)} then an Exception can be thrown.
</#macro>