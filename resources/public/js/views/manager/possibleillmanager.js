/**
 * Created by jack on 5/14/15.
 */
define(function () {

    function render(parameters) {
        $('#possibleillmanagerpanel').datagrid({
            singleSelect: true,
            collapsible: true,
            rownumbers: true,
            method:'post',
            fitColumns:true,
            url:'../hospital/getpossibleillsbypage',
            remoteSort: false,
            /*sortName:'time',
             sortOrder:'desc',*/
            fit:true,
            //toolbar:'#packagepaneltb',
            pagination:true,
            pageSize:10,


            toolbar:'#packagepaneldetailtb',
            onBeforeLoad: function (params) {
                //alert(1);
                var options = $('#possibleillmanagerpanel').datagrid('options');
                params.start = (options.pageNumber - 1) * options.pageSize;
                params.limit = options.pageSize;
                params.totalname = "total";
                params.rowsname = "rows";
            }/*,
            onClickRow:onClickRow*/

        });

    }

    return {
        render: render

    };
});