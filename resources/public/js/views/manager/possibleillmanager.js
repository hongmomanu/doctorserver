/**
 * Created by jack on 5/14/15.
 */
define(function () {

    function render(parameters) {


        $.extend($.fn.datagrid.methods, {
            editCell: function(jq,param){
                return jq.each(function(){
                    var opts = $(this).datagrid('options');
                    var fields = $(this).datagrid('getColumnFields',true).concat($(this).datagrid('getColumnFields'));
                    for(var i=0; i<fields.length; i++){
                        var col = $(this).datagrid('getColumnOption', fields[i]);
                        col.editor1 = col.editor;
                        if (fields[i] != param.field){
                            col.editor = null;
                        }
                    }
                    $(this).datagrid('beginEdit', param.index);
                    var ed = $(this).datagrid('getEditor', param);
                    if (ed){
                        if ($(ed.target).hasClass('textbox-f')){
                            $(ed.target).textbox('textbox').focus();
                        } else {
                            $(ed.target).focus();
                        }
                    }
                    for(var i=0; i<fields.length; i++){
                        var col = $(this).datagrid('getColumnOption', fields[i]);
                        col.editor = col.editor1;
                    }
                });
            },
            enableCellEditing: function(jq){
                return jq.each(function(){
                    var dg = $(this);
                    var opts = dg.datagrid('options');
                    opts.oldOnClickCell = opts.onClickCell;
                    opts.onClickCell = function(index, field){
                        if (opts.editIndex != undefined){
                            if (dg.datagrid('validateRow', opts.editIndex)){
                                dg.datagrid('endEdit', opts.editIndex);
                                opts.editIndex = undefined;
                            } else {
                                return;
                            }
                        }
                        dg.datagrid('selectRow', index).datagrid('editCell', {
                            index: index,
                            field: field
                        });
                        opts.editIndex = index;
                        opts.oldOnClickCell.call(this, index, field);
                    }
                });
            }
        });


        var accept=function (){


            var inserted=$('#possibleillmanagerpanel').datagrid('getChanges','inserted');
            var deleted=$('#possibleillmanagerpanel').datagrid('getChanges','deleted');
            var updated=$('#possibleillmanagerpanel').datagrid('getChanges','updated');
            if(inserted.length>0){

            }

            if(updated.length>0){

                require(['../js/commonfuncs/AjaxForm.js']
                    ,function(ajaxfrom){

                        var success=function(){
                            $.messager.alert('操作成功','成功!');
                            $('#possibleillmanagerpanel').datagrid('acceptChanges');
                            $('#possibleillmanagerpanel').datagrid('reload');
                        };
                        var errorfunc=function(){
                            $.messager.alert('操作失败','失败!');
                        };
                        var params= {illdata:$.toJSON(updated)};
                        ajaxfrom.ajaxsend('post','json','../hospital/editilldata',params,success,null,errorfunc);

                    });


            }




                //console.log(inserted);
                //console.log(deleted);
                //console.log(updated);


        }

        
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
            toolbar:'#possibleillpaneltb',
            pagination:true,
            pageSize:10,
            onBeforeLoad: function (params) {
                //alert(1);
                var options = $('#possibleillmanagerpanel').datagrid('options');
                params.start = (options.pageNumber - 1) * options.pageSize;
                params.limit = options.pageSize;
                params.totalname = "total";
                params.rowsname = "rows";
            }

        });

        $('#possibleillmanagerpanel').datagrid('enableCellEditing');

        $('#possibleillpaneltb .save').click(accept);

    }

    return {
        render: render

    };
});