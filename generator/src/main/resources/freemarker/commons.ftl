<#macro type classType packageName>
<@compress single_line=true>
    <#if classType?matches(packageName?replace(".","\\.")+"\\.[A-Z].*")>
    	${classType?remove_beginning("java.lang.")?remove_beginning(packageName+".")}
    <#else>
    	${classType?remove_beginning("java.lang.")}
    </#if>
</@compress>
</#macro>

<#macro internalBuilderType classType packageName parent>
<@compress single_line=true>
    <#if classType?matches(packageName?replace(".","\\.")+"\\.[A-Z].*")>
    	${classType?remove_beginning(parent+".")?remove_beginning("java.lang.")?remove_beginning(packageName+".")}
    <#else>
    	${classType?remove_beginning(parent+".")?remove_beginning("java.lang.")}
    </#if>
</@compress>
</#macro>


<#macro builderType member packageName sourceClassName>
<@compress single_line=true>
    <#if member.outerType?? && member.outerType != sourceClassName>
        <#if member.hasSubType()>
            <@type member.outerType packageName/>Builder.<@internalBuilderType member.subType packageName member.outerType/>Builder
        <#else>
            <@type member.outerType packageName/>Builder.<@internalBuilderType member.type packageName member.outerType/>Builder
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
