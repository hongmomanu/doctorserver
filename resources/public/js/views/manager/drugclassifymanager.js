/**
 * Created by jack on 5/14/15.
 */
define(function () {

    function render(parameters) {

        $('#drugclassifytree').tree({

            url: '../hospital/getclassifytree',
            method: 'get',
            animate: true,
            onClick: function(node){
                $(this).tree('beginEdit',node.target);
            },
            onAfterEdit:function(node){
                console.log(node);
                require(['../js/commonfuncs/AjaxForm.js']
                    ,function(ajaxfrom){

                        var success=function(){
                            $.messager.alert('操作成功','成功!');
                            var parent= $('#drugclassifytree').tree('getParent',node);
                            $('#drugclassifytree').tree('reload',parent);
                        };
                        var errorfunc=function(){
                            $.messager.alert('操作失败','失败!');
                        };
                        var params= {name:node.text,parentid:node.parentid,_id:node.id};
                        ajaxfrom.ajaxsend('post','json','../hospital/updateorinsertclassify',params,success,null,errorfunc);

                    });

            },

            onContextMenu: function(e,node){
                e.preventDefault();
                $(this).tree('select',node.target);
                $('#drugclassifytree-mm').menu('show',{
                    left: e.pageX,
                    top: e.pageY
                });
            }

        });

        var add=function(){
            var t = $('#drugclassifytree');
            var node = t.tree('getSelected');
            t.tree('append', {
                parent: (node?node.target:null),
                data: [{
                    text: '未保存,请编辑',
                    parentid:node.id
                }]
            });
        };
        var remove=function(){
            alert(1);
        };


        $('#drugclassifytree-mm .add').click(add);
        $('#drugclassifytree-mm .remove').click(remove);



    }

    return {
        render: render

    };
});