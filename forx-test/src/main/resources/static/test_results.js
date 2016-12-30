function toggleDetail(where)
{
    $(where).find("+ .testDetail").toggle("fast");
}

function setStatus(idOfSelected, newStatus)
{
    var underscorePos=idOfSelected.lastIndexOf("_");
    var index=idOfSelected.substring(underscorePos+1);

    $('#'+idOfSelected+' .testname .result').removeClass("pass fail nottested");
    $('#'+idOfSelected+' .testname .result').addClass(newStatus);
    $('#'+idOfSelected+' .testDetail [name=\'value['+(parseInt(index)-1)+'].result\']').val(newStatus);
}

function setStatusAndMoveActive(newStatus)
{
    var idOfSelected=$('div.active').attr('id');

    setStatus(idOfSelected, newStatus);
    
    scrollDown();
}

function toggleAStatus(isChecked, status)
{
    if(isChecked)
        $('.testDetail.'+status).show('fast');
    else
        $('.testDetail.'+status).hide('fast');
}

function scrollDown()
{
    var idOfSelected=$('div.active').attr('id');
    var underscorePos=idOfSelected.lastIndexOf("_");
    var index=idOfSelected.substring(underscorePos+1);
    var nextIdToSelect=idOfSelected.replace(index, parseInt(index)+1);

    if($('#'+nextIdToSelect).length) {
        $('#' + idOfSelected).removeClass("active");
        $('#' + nextIdToSelect).addClass("active");

        $('#' + nextIdToSelect).get(0).scrollIntoView();
    }
}

function scrollUp()
{
    var idOfSelected=$('div.active').attr('id');
    var underscorePos=idOfSelected.lastIndexOf("_");
    var index=idOfSelected.substring(underscorePos+1);
    if(index>1)
    {
        var nextIdToSelect = idOfSelected.replace(index, parseInt(index) - 1);

        $('#' + idOfSelected).removeClass("active");
        $('#' + nextIdToSelect).addClass("active");

        $('#' + nextIdToSelect).get(0).scrollIntoView();
    }
}

$(function()
{
    $('html').keydown(function(e)
    {
        if(e.which==40) // down
        {
            scrollDown();
            e.preventDefault();
        }
        else if(e.which==38) // up
        {
            scrollUp();
            e.preventDefault();
        }
        else if(e.which==80) // p
        {
            setStatusAndMoveActive("pass");
        }
        else if(e.which==70) // f
        {
            setStatusAndMoveActive("fail");
        }
        else if(e.which==78) // n
        {
            setStatusAndMoveActive("nottested");
        }
    });
});
