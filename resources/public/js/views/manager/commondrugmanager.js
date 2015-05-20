/**
 * Created by jack on 5/14/15.
 */
define(function () {

    function render(parameters) {


        var editIndex=-1;
        var append=function(){

            $('#commondrugmanager').datagrid('appendRow',{});
            editIndex = $('#commondrugmanager').datagrid('getRows').length-1;
            $('#commondrugmanager').datagrid('selectRow', editIndex)
                .datagrid('beginEdit', editIndex);
        };



        var accept=function (){


            var inserted=$('#commondrugmanager').datagrid('getChanges','inserted');
            var deleted=$('#commondrugmanager').datagrid('getChanges','deleted');
            var updated=$('#commondrugmanager').datagrid('getChanges','updated');
            if(inserted.length>0){


                require(['../js/commonfuncs/AjaxForm.js']
                    ,function(ajaxfrom){

                        var success=function(){
                            $.messager.alert('操作成功','成功!');
                            $('#commondrugmanager').datagrid('acceptChanges');
                            $('#commondrugmanager').datagrid('reload');
                        };
                        var errorfunc=function(){
                            $.messager.alert('操作失败','失败!');
                        };
                        var params= {commondata:$.toJSON(inserted)};
                        ajaxfrom.ajaxsend('post','json','../hospital/insertcommondrugdata',params,success,null,errorfunc);

                    });

            }

            if(updated.length>0){

                require(['../js/commonfuncs/AjaxForm.js']
                    ,function(ajaxfrom){

                        var success=function(){
                            $.messager.alert('操作成功','成功!');
                            $('#commondrugmanager').datagrid('acceptChanges');
                            $('#commondrugmanager').datagrid('reload');
                        };
                        var errorfunc=function(){
                            $.messager.alert('操作失败','失败!');
                        };
                        var params= {commondata:$.toJSON(updated)};
                        ajaxfrom.ajaxsend('post','json','../hospital/editcommondrugdata',params,success,null,errorfunc);

                    });


            }




                //console.log(inserted);
                //console.log(deleted);
                //console.log(updated);


        }

        
        $('#commondrugmanager').datagrid({
            singleSelect: true,
            collapsible: true,
            rownumbers: true,
            method:'post',
            fitColumns:true,
            url:'../hospital/getcommondrugsbypage',
            remoteSort: false,
            /*sortName:'time',
             sortOrder:'desc',*/
            fit:true,
            toolbar:'#commondrugpaneltb',
            onClickRow:function(index,row){
                if(index!=editIndex)$('#commondrugmanager').datagrid('endEdit',editIndex);
            },
            pagination:true,
            pageSize:10,
            onBeforeLoad: function (params) {
                //alert(1);
                var options = $('#commondrugmanager').datagrid('options');
                params.start = (options.pageNumber - 1) * options.pageSize;
                params.limit = options.pageSize;
                params.totalname = "total";
                params.rowsname = "rows";
            }

        });

        $('#commondrugmanager').datagrid('enableCellEditing');

        $('#commondrugpaneltb .save').click(accept);
        $('#commondrugpaneltb .append').click(append);


    }

    return {
        render: render

    };
});