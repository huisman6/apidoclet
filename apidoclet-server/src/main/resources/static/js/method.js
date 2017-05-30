/**
 * Created by charles on 16/1/27.
 */

(function () {
    $('.list > ul.parent').on('click', function () {
        $('li:first', this).toggleClass('active');
        $('.child', this).slideToggle(300);
    });

    $('tr.parent').on('click', function () {
        $('tr:first', $(this).parent()).toggleClass('active');
        $('tr.child', $(this).parent()).slideToggle(300);
    });
})();

(function(window){
    var ToolTip = {
        template: '{value}',
        init: function(){
            var template = this.template;
            $('[title]').addClass('tooltip-container')
                .each(function(index, element){
                    $(element).append($('<div class="tooltip">' + template.replace(/\{value\}/g,element.title) + '</div>'));
                });
            $('[title]').off('mouseenter').on('mouseenter', function(e){
                $('.tooltip', this).show();
                this.attributes.originTitle = this.title;
                this.title  = '';
                e.preventDefault();
                return false;
            }).off('mouseleave').on('mouseleave', function(e){
                $('.tooltip', this).hide();
                this.title  = this.attributes.originTitle;
            });
        }
    };

    var Affix = {
        init: function(target, opts){
            opts = $.extend({}, opts);
            var top = opts.top,
                bottom = opts.bottom;
            $(document).on('scroll', function(e){
                if($(document).scrollTop() > top) {
                    target.addClass('affix');
                } else {
                    target.removeClass('affix');
                }
            });
        }
    };

    window.ToolTip = ToolTip;
    window.Affix = Affix;
})(window);
ToolTip.init();
Affix.init($('.right_container'), {
    top: $('.header_container').height() + $('.logo_menu').height(),
    bottom: $('.footer').height()
});
