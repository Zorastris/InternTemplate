//This is how you get an element with CSS selector, do some js to change text
//ID for the create button is "create_link"
AJS.toInit(function () {
    document.getElementById('create_link').text = 'Generate';
    console.log('Changed inner text');
});
