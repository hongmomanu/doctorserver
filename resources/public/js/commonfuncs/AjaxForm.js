/**
 * Created by jack on 14-1-7.
 */
define(function(){

    var a={

       ajaxform:function(form,url,onsubmit,success){
           $.messager.progress();
           form.form('submit', {
                   url: url,
                   onSubmit:onsubmit,
                   success:function(res){
                       $.messager.progress('close');
                       if(success)success(res);
                   }

           });

       },
       ajaxsend:function(method,type,url,params,success,complete,errorfunc,hideprogress,isasync){

           if(!hideprogress)$.messager.progress('close');
           var compfunc=function(){
               if(!hideprogress)$.messager.progress('close');
               if(complete)complete();
           };
           $.ajax({
               type: method,
               dataType: type,
               async:!isasync,
               url: url,
               data: params,
               complete :compfunc,
               error:errorfunc,
               success: success
           });
       }


    };
    return a;
});
