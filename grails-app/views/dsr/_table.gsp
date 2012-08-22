<g:render template="/templates/reportTitle" model="[program: currentProgram, title: i18n(field:currentProgram.names), file: 'star_small.png']"/>
<g:render template="/templates/reportProgramParent"/>

<g:reportView linkParams="${params}" exclude="${['indicators']}"/>
<g:reportCategoryFilter linkParams="${params}" exclude="${['indicators']}"/>

<g:render template="/dsr/reportProgramTable"/>