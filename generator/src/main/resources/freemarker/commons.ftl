<#macro type classType packageName>
<@compress single_line=true>
	${classType.getType(packageName)}
</@compress>
</#macro>

<#macro typeWithoutGenerics classType packageName>
<@compress single_line=true>
    ${classType.getTypeWithoutGenerics(packageName)}
</@compress>
</#macro>

<#macro internalBuilderType classType packageName parent>
<@compress single_line=true>
    <#if classType.getPackageName() == packageName>
        ${classType.getType()?remove_beginning(parent.getType()+".")?remove_beginning(packageName+".")}
    <#else>
        ${classType.getType()?remove_beginning(parent.getType()+".")}
    </#if>
</@compress>
</#macro>

<#macro builderType member packageName sourceClassName>
<@compress single_line=true>
    <#if member.outerTypes?size gt 0 >
        <#if member.hasSubType()>
            <@builderTypeInnerClasses member.subType member.outerTypes packageName sourceClassName/><#t>
        <#else>
            <@builderTypeInnerClasses member.type member.outerTypes packageName sourceClassName/><#t>
        </#if>
    <#else>
        <#if member.hasSubType()>
            <@internalBuilderType member.subType packageName sourceClassName/>Builder
        <#else>
            <@internalBuilderType member.type packageName sourceClassName/>Builder
        </#if>
    </#if>
</@compress>
</#macro>

<#macro builderTypeInnerClasses type outerTypes packageName sourceClassName>
<@compress single_line=true>
    <#list outerTypes>
        <#assign previousOuterType = sourceClassName>
        <#items as outerType>
            <#if outerType == sourceClassName && outerTypes?size == 1>
                <#continue>
            </#if>
            <@internalBuilderType outerType packageName previousOuterType/>Builder.<#t>
            <#assign previousOuterType = outerType>
        </#items>
        <@internalBuilderType type packageName previousOuterType/>Builder<#t>
    </#list>
</@compress>
</#macro>
