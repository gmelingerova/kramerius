<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/tlds/securedContent.tld" prefix="scrd" %>
<div id="dateAxis" class="shadow box" style="float:right;right:0;z-index:2;background:white;position:absolute;">
<div id="showHideDA" ><a href="javascript:toggleDA();" title="show/hide date axis"><span class="ui-state-default ui-icon ui-icon-image ui-icon-circle-triangle-e"></span></a></div>
    <div id="daBox">
        <%@include file="../dateAxisV.jsp" %>
    </div>
</div>
<div id="filters">
    <ul><li><a href="#facets"><fmt:message bundle="${lctx}">results.filters</fmt:message></a></li></ul>
    <div id="facets">
    <%@ include file="../usedFilters.jsp" %>
    <%@ include file="../facets.jsp" %>
    </div>
</div>
<div id="docs">
    <ul><li><a href="#docs_content"><fmt:message bundle="${lctx}">results.results</fmt:message></a></li></ul>
    <div id="docs_content">
    <%--&#160;<c:out value="${numDocs}" />&#160;<c:out value="${numDocsStr}" />--%>
    <%@ include file="docs.jsp" %>
    </div>
</div>
<script type="text/javascript">


$(document).ready(function(){
    $('.loading_docs').hide();
    $("#filters").tabs();
    $("#docs").tabs();
    
    $(document).bind('scroll', function(event){
        var id = $('#docs .more_docs').attr('id');
        if($('#docs .more_docs').length>0){
            if(isScrolledIntoWindow($('#'+id))){
                getMoreDocs(id);
                //alert(id);
            }
        }
    });
    
    if($('#docs .more_docs').length>0){
        var id = $('#docs .more_docs').attr('id');
        if(isScrolledIntoWindow($('#'+id))){
            getMoreDocs(id);
            //alert(id);
        }
    }


<%  if (request.getRemoteUser() != null) {%>
        $('.result').append('<input type="checkbox" />');
<% }%>
    checkHeight(0);
});
    
    function toggleColumns(){
        var margin = parseInt($('.search_result:first').css("margin-left").replace("px", "")) +
            parseInt($('.search_result:first').css("margin-right").replace("px", "")) +
            parseInt($('.search_result:first').css("padding-left").replace("px", "")) +
            parseInt($('.search_result:first').css("padding-right").replace("px", ""));
        var w = $('#offset_0').width();
        //alert(margin);
        if($('#cols').is(':checked')){
            w = w - margin;
        }else{
            w = w / 2 - margin * 2;
        }
        $('.search_result').css('width', w);
    }

    function checkHeight(offset){
        var divs = $('#offset_'+offset+'>div.search_result').length;
        var left;
        var right;
        var max;
        for(var i=1; i<divs; i = i+2){
            left = $('#offset_'+offset+'>div.search_result')[i-1];
            right = $('#offset_'+offset+'>div.search_result')[i];
            checkRowHeight(left, right);
        }
    }
    
    function checkRowHeight(left, right){
        var max;
        max = Math.max($(left).height(), $(right).height());
        var id1 = $(left).attr('id');
        max = Math.max(max, $(jq(id1)+' img.th').height());
        var id2 = $(right).attr('id');
        max = Math.max(max, $(jq(id2)+' img.th').height());
        $(left).css('height', max);
        $(right).css('height', max);
    }
    
    function resultThumbLoaded(obj){
        var div = $(obj).parent().parent().parent().parent();
        //alert($(div).attr('class'));
        if($(div).hasClass('0')){
            var div2 = $(div).prev();
            checkRowHeight(div2, div);
        }else{
            //var div2 = $(div).prev();
            //checkRowHeight(div2, div);
        }
    }

    function getMoreDocs(id){
        var offset = id.split('_')[1];
        var page = new PageQuery(window.location.search);
        page.setValue("offset", offset);
        var url =  "r.jsp?onlymore=true&" + page.toString();
        $.get(url, function(data) {
            $(jq(id)).html(data);
            $(jq(id)).removeClass('more_docs');
            $('.loading_docs').hide();
            checkHeight(offset);
<scrd:loggedusers>
            $(jq(id)+' .result').append('<input type="checkbox" />');
</scrd:loggedusers>
        });
    }
    
    function sortByTitle(dir){
        $('#sort').val('root_title '+dir);
        $('#searchForm').submit();
    }
    
    function sortByRank(){
        $('#sort').val('level asc, score desc');
        $('#searchForm').submit();
    }

    function addFilter(field, value){
        var page = new PageQuery(window.location.search);
        page.setValue("offset", "0");
        var f = "fq=" + field + ":\"" + value + "\"";
        if(window.location.search.indexOf(f)==-1){
            window.location = "r.jsp?" +
            page.toString() + "&" + f;
        }
    }

    function toggleCollapsed(root_pid, pid, offset){
        $(jq("res_"+root_pid)+">div.uncollapsed").toggle();
        $(jq('uimg_' + root_pid) ).toggleClass('uncollapseIcon');
        if($(jq("res_"+root_pid)+">div.uncollapsed").html()==""){
            uncollapse(root_pid, pid, offset);
        }
    }
    function uncollapse(root_pid, pid, offset){
          var page = new PageQuery(window.location.search);
          page.setValue("offset", offset);
          var url =  "inc/results/uncollapse.jsp?rows=10&" + page.toString() +
              "&type=uncollapse&collapsed=false&root_pid=" + root_pid +
              "&pid=" + pid +
              "&fq=root_pid:\"" + root_pid + "\"" + "&fq=-PID:\"" + pid + "\"";
          $.get(url, function(xml) {
              $(jq("res_"+root_pid)+">div.uncollapsed").html(xml);
              $(jq("res_"+root_pid)+">div.uncollapsed").scrollTop(0);
          });
    }
</script>