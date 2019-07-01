(function ($) {

    $.menu = {

        load: function () {
            this.bind();
        },

        showWaiting: function () {
            if ($("#winModal")[0]) {
                $("#winModal").addClass("in");
            } else {
                $("body").append("<div id='winModal' name='div_index_winModal' class='modal-backdrop fade in' style='z-index:9999'></div><div id='loadInfo' name='div_index_winModal'>请稍候...</div>");
            }
        },

        hideWaiting: function () {
            $("div[name='div_index_winModal']").each(function () {
                $(this).remove();
            });
        },

        bind: function () {
            $(".submenu a").click(function(){
                $.menu.showWaiting();
                $(".nav-list li").removeClass("active");
                $(this).parent("li").addClass("active").parents("li.open").addClass("active");
                var url = $(this).attr("data-url");
                $(".main-content .main-content-inner").html("");
                $(".main-content .main-content-inner").load(url);
                $.menu.hideWaiting();
            });
        }

    };

    $.menu.load();

})(jQuery);

/*
var time1 = new Date().format("yyyy-MM-dd HH:mm:ss");     
var time2 = new Date().format("yyyy-MM-dd"); 
 
* */
Date.prototype.Format = function(fmt)   
{
 var o = {   
   "M+" : this.getMonth()+1,                 //月份   
   "d+" : this.getDate(),                    //日   
   "h+" : this.getHours(),                   //小时   
   "m+" : this.getMinutes(),                 //分   
   "s+" : this.getSeconds(),                 //秒   
   "q+" : Math.floor((this.getMonth()+3)/3), //季度   
   "S"  : this.getMilliseconds()             //毫秒   
 };   
 if(/(y+)/.test(fmt))   
   fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));   
 for(var k in o)   
   if(new RegExp("("+ k +")").test(fmt))   
 fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));   
 return fmt;   
}

/**       
* 对Date的扩展，将 Date 转化为指定格式的String       
* 月(M)、日(d)、12小时(h)、24小时(H)、分(m)、秒(s)、周(E)、季度(q) 可以用 1-2 个占位符       
* 年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字)       
* eg:       
* (new Date()).pattern("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423       
* (new Date()).pattern("yyyy-MM-dd E HH:mm:ss") ==> 2009-03-10 二 20:09:04       
* (new Date()).pattern("yyyy-MM-dd EE hh:mm:ss") ==> 2009-03-10 周二 08:09:04       
* (new Date()).pattern("yyyy-MM-dd EEE hh:mm:ss") ==> 2009-03-10 星期二 08:09:04       
* (new Date()).pattern("yyyy-M-d h:m:s.S") ==> 2006-7-2 8:9:4.18       
*/          
Date.prototype.pattern=function(fmt) {           
   var o = {           
   "M+" : this.getMonth()+1, //月份           
   "d+" : this.getDate(), //日           
   "h+" : this.getHours()%12 == 0 ? 12 : this.getHours()%12, //小时           
   "H+" : this.getHours(), //小时           
   "m+" : this.getMinutes(), //分           
   "s+" : this.getSeconds(), //秒           
   "q+" : Math.floor((this.getMonth()+3)/3), //季度           
   "S" : this.getMilliseconds() //毫秒           
   };           
   var week = {           
   "0" : "/u65e5",           
   "1" : "/u4e00",           
   "2" : "/u4e8c",           
   "3" : "/u4e09",           
   "4" : "/u56db",           
   "5" : "/u4e94",           
   "6" : "/u516d"          
   };           
   if(/(y+)/.test(fmt)){           
       fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));           
   }           
   if(/(E+)/.test(fmt)){           
       fmt=fmt.replace(RegExp.$1, ((RegExp.$1.length>1) ? (RegExp.$1.length>2 ? "/u661f/u671f" : "/u5468") : "")+week[this.getDay()+""]);           
   }           
   for(var k in o){           
       if(new RegExp("("+ k +")").test(fmt)){           
           fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));           
       }           
   }           
   return fmt;           
} 

function parseDate(str)
{   
	if(typeof str == 'string'){   
		var results = str.match(/^ *(\d{4})-(\d{1,2})-(\d{1,2}) *$/);   
		if(results && results.length>3)   
			return new Date(parseInt(results[1]),parseInt(results[2]) -1,parseInt(results[3]));    
		results = str.match(/^ *(\d{4})-(\d{1,2})-(\d{1,2}) +(\d{1,2}):(\d{1,2}):(\d{1,2}) *$/);   
		if(results && results.length>6)   
			return new Date(parseInt(results[1]),parseInt(results[2]) -1,parseInt(results[3]),parseInt(results[4]),parseInt(results[5]),parseInt(results[6]));    
		results = str.match(/^ *(\d{4})-(\d{1,2})-(\d{1,2}) +(\d{1,2}):(\d{1,2}):(\d{1,2})\.(\d{1,9}) *$/);   
		if(results && results.length>7)   
			return new Date(parseInt(results[1]),parseInt(results[2]) -1,parseInt(results[3]),parseInt(results[4]),parseInt(results[5]),parseInt(results[6]),parseInt(results[7]));    
	}   
	return null;
}   
       
Date.prototype.addDays = function(d)
{
    this.setDate(this.getDate() + d);
};

Date.prototype.addWeeks = function(w)
{
    this.addDays(w * 7);
};

Date.prototype.addMonths= function(m)
{
    var d = this.getDate();
    this.setMonth(this.getMonth() + m);
    if (this.getDate() < d)
        this.setDate(0);
};

Date.prototype.addYears = function(y)
{
    var m = this.getMonth();
    this.setFullYear(this.getFullYear() + y);
    if (m < this.getMonth())
     {
        this.setDate(0);
     }
};


function display_message(msg){
	
	
		jQuery.gritter.add({
			title: '内容',
			text: ""+decodeURI(msg)+"",
			class_name: 'gritter-info gritter-center gritter-light'
		});

	

}

function dialog_message_modal(msg){
	
	jQuery( "#dialog-message" ).html(decodeURI(msg));
	
	var dialog = jQuery( "#dialog-message" ).removeClass('hide').dialog({
		modal: true,
		title: "<div class='widget-header widget-header-small'><h4 class='smaller'><i class='ace-icon fa fa-check'></i>消息</h4></div>",
		title_html: true,
		width: 400,
		height: 200,
		buttons: [ 
			{
				text: "关闭",
				"class" : "btn btn-xs",
				click: function() {
					$( this ).dialog( "close" ); 
				} 
			}
		]
	});
}

function dialog_warn_modal(msg){
	
	jQuery( "#dialog-message" ).html(decodeURI(msg));
	
	jQuery( "#dialog-message" ).removeClass('hide').dialog({
		resizable: false,
		modal: true,
		title: "<div class='widget-header'><h4 class='smaller'><i class='ace-icon fa fa-exclamation-triangle red'></i>提示</h4></div>",
		title_html: true,
		width: 400,
		height: 200,
		buttons: [
			{
				html: "<i class='ace-icon fa fa-times bigger-110'></i>&nbsp;确认",
				"class" : "btn btn-xs",
				click: function() {
					$( this ).dialog( "close" );
				}
			}
		]
	});
}



function dialog_biz_modal(uri, title, model_width, model_height){
	
	//get_request_dialog(uri, '#biz-detail', true);
	
	var dialog = $( "#biz-detail" ).removeClass('hide').dialog({
		modal: true,
		title: "<div class='widget-header widget-header-small'><h4 class='smaller'>"+title+"</h4></div>",
		title_html: true,
		width: model_width,
		height: model_height,
		buttons: [ 
			{
				text: "关闭",
				"class" : "btn btn-xs",
				click: function() {
					$( this ).dialog( "close" ); 
				} 
			}
		]
	});
}

function get_request_dialog(url, dialogId, modal){
	
	
}

function request_ajax_form(uri, fromId, flushId, modal, isCallback) {
	
    try {
        jQuery.ajax({
            url: uri,
            type: 'post',
            cache: false,
            global: false,
            async: true,
            contentType: 'application/x-www-form-urlencoded;charset=utf-8',
            data: fromId.serialize(),
            dataType: 'html',
            beforeSend: function(xmlHttpRequest) {
                if (modal) {
                	jQuery.menu.showWaiting();
                }
            },
            complete: function(xmlHttpRequest, textStatus) {
                
            	jQuery.menu.hideWaiting();
            	if(isCallback){
            		asynCallback();
            	}
            },
            success: function(content) {
                var db = $(content);
                try {
                	jQuery(flushId).html(db.find(flushId).html());
                } catch(e) {
                	jQuery.menu.hideWaiting();
                	alert('[ERROR] - ' + e);
                }
                
            },
            error: function(xmlHttpRequest, textStatus, errorThrown) {
            	jQuery.menu.hideWaiting();
                $(document.body).html('<div style="overflow: hidden; overflow-y: auto;">' + xmlHttpRequest.responseText + '</div>');
            }
        });
    } catch(e) {
    	jQuery.menu.hideWaiting();
        alert('[ERROR] - ' + e);
    }
}