/**
 * 图片展示
 * @author 韩春阳
 * @date 2016-06-13
 */
$(function () {
    //currentIndex && requestUrl
    var url = '';//请求地址
    var index = 0;//请求索引
    var host = 'http://192.168.3.89:9999';//服务器域名
    //html Dom list
    var mainDom = $('.main-container');//图片container
    var detailDom = $('.detail-container');//故事container
    var videoDom = $('#vid');//video dom
    var videoBtnDom = $('.icon-video');//video button dom
    //请求图片 parts of url
    var getUrlHead = host + '/proxy/get?url=http://cn.bing.com/HPImageArchive.aspx&format=js&idx=';
    var getUrlDetail = '&n=1&video=1';
    //请求图片故事 parts of url
    var getInfoUrlHead = host + '/proxy/get?url=http://cn.bing.com/cnhp/life&currentDate=';
    var getInfoUrlDetail = '&IID=SERP.5048&IG=0DC4E1AFCE78435F9C80615110D5A14D';

    //首次加载
    $.ajax({url: getUrlHead + index + getUrlDetail}).success(function (res) {
        var result = JSON.parse(res);
        mainDom.css({'background-image': 'url(' + result.images[0].url + ')'});
        getInfo(result);
    }).error(function (res) {
        console.error(res);
    });
    //上一图
    $('.icon-left').click(function () {
        index--;
        var getUrl = getUrlHead + index + getUrlDetail;
        $.ajax({url: getUrl}).success(function (res) {
            var result = JSON.parse(res);
            var image = new Image();
            mainDom.fadeOut(300, function(){
                image.src = result.images[0].url;
                image.addEventListener('load',function(d){
                    mainDom.css({'background-image': 'url(' + result.images[0].url + ')'});
                    mainDom.fadeIn(300);
                    getInfo(result, image);
                });
            });
        }).error(function (res) {
            index++;
            console.error(res);
        });
    });
    //下一图
    $('.icon-right').click(function () {
        index++;
        var getUrl = getUrlHead + index + getUrlDetail;
        $.ajax({url: getUrl}).success(function (res) {
            var result = JSON.parse(res);
            var image = new Image();
            mainDom.fadeOut(300, function(){
                image.src = result.images[0].url;
                image.addEventListener('load',function(d){
                    mainDom.css({'background-image': 'url(' + result.images[0].url + ')'});
                    mainDom.fadeIn(300);
                    getInfo(result, image);
                });
            });
        }).error(function (res) {
            index--;
            console.error(res);
        });
    });
    //播放暂停
    videoBtnDom.click(function () {
        videoBtnDom.toggleClass('pause').hasClass('pause') ? videoDom[0].pause() : videoDom[0].play();
    });
    //展示简介
    $('.icon-font').click(function () {
        detailDom[0].style.right == '0px' ? detailDom.animate({'right': '-440px'}) : detailDom.animate({'right': '0px'});
    });
    //收起简介
    mainDom.click(function () {
        detailDom.animate({'right': '-440px'});
    });
    //加载video及简介
    function getInfo(value) {
        var currentDate = value.images[0].enddate;
        videoDom.css({'display': 'none'});
        videoBtnDom.removeClass('pause').css({'display': 'none'});
        //加载video
        if (value.images[0].vid) {
            var videoUrl = value.images[0].vid.sources[0][2];
            videoDom.attr('src', videoUrl).css({'display': 'block'});
            videoDom[0].play();
            videoBtnDom.css({'display': 'block'});
        }
        //加载简介
        $.ajax({url: getInfoUrlHead + currentDate + getInfoUrlDetail}).success(function (res) {
            detailDom.empty().append(res);
            detailDom.prepend('<div style=" position: fixed;height: 100px;width: 100%;bottom: 0px;background-color: black;z-index: 1;"></div>');
            $('#hpla').css({'padding': '0 40px 100px 40px'});
        }).error(function (res) {
            console.error(res);
        });
    }
});


