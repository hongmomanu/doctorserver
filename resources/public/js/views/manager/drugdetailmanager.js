/**
 * Created by jack on 5/14/15.
 */
define(function () {

    function render(parameters) {



        var editIndex=-1;
        var append=function(){

                $('#drugdetailmanagerpanel').datagrid('appendRow',{});
                editIndex = $('#drugdetailmanagerpanel').datagrid('getRows').length-1;
                $('#drugdetailmanagerpanel').datagrid('selectRow', editIndex)
                    .datagrid('beginEdit', editIndex);
        };


        var accept=function (){


            var inserted=$('#drugdetailmanagerpanel').datagrid('getChanges','inserted');
            var deleted=$('#drugdetailmanagerpanel').datagrid('getChanges','deleted');
            var updated=$('#drugdetailmanagerpanel').datagrid('getChanges','updated');
            if(inserted.length>0){

            }

            if(updated.length>0){

                require(['../js/commonfuncs/AjaxForm.js']
                    ,function(ajaxfrom){

                        var success=function(){
                            $.messager.alert('操作成功','成功!');
                            $('#drugdetailmanagerpanel').datagrid('acceptChanges');
                            $('#drugdetailmanagerpanel').datagrid('reload');
                        };
                        var errorfunc=function(){
                            $.messager.alert('操作失败','失败!');
                        };
                        var params= {illdata:$.toJSON(updated)};
                        ajaxfrom.ajaxsend('post','json','../hospital/editdrugdata',params,success,null,errorfunc);

                    });


            }




                //console.log(inserted);
                //console.log(deleted);
                //console.log(updated);


        }

        
        $('#drugdetailmanagerpanel').datagrid({
            singleSelect: true,
            collapsible: true,
            rownumbers: true,
            method:'post',
            fitColumns:true,
            url:'../hospital/getdrugsbypage',
            remoteSort: false,
            /*sortName:'time',
             sortOrder:'desc',*/
            fit:true,
            toolbar:'#drugdetailpaneltb',
            onClickRow:function(index,row){
                if(index!=editIndex)$('#drugdetailmanagerpanel').datagrid('endEdit',editIndex);
            },
            pagination:true,
            pageSize:10,
            onBeforeLoad: function (params) {
                //alert(1);
                var options = $('#drugdetailmanagerpanel').datagrid('options');
                params.start = (options.pageNumber - 1) * options.pageSize;
                params.limit = options.pageSize;
                params.totalname = "total";
                params.rowsname = "rows";
            }

        });

        $('#drugdetailmanagerpanel').datagrid('enableCellEditing');

        $('#drugdetailpaneltb .save').click(accept);
        $('#drugdetailpaneltb .append').click(append);

    }

    return {
        render: render

    };
});