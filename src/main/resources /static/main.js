// প্রথমে আমরা HTML থেকে ফর্মটিকে ধরব
const myForm = document.getElementById('userForm');

// ফর্ম সাবমিট হলে কী ঘটবে তার নির্দেশনা
myForm.addEventListener('submit', function (event) {
    
    // (খুব গুরুত্বপূর্ণ) ব্রাউজারকে পেজ রিলোড করা থেকে বিরত রাখা
    event.preventDefault(); 

    // ফর্মের ভেতরের সব ডেটা সংগ্রহ করা
    const formData = new FormData(myForm);

    // fetch API ব্যবহার করে ডেটা 'welcome.php'-তে পাঠানো
    fetch('welcome.php', {
        method: 'POST', // আমরা POST মেথডে ডেটা পাঠাচ্ছি
        body: formData   // ফর্মের ডেটা বডিতে পাঠানো হচ্ছে
    })
    .then(response => response.text()) // PHP থেকে যে উত্তর আসবে তা টেক্সট হিসেবে পড়া
    .then(data => {
        // PHP থেকে আসা উত্তরটি দেখানো
        console.log(data); 
        alert(data); // ব্যবহারকারীকে একটি পপ-আপ মেসেজ দেখানো

        // ফর্মটি খালি করে দেওয়া (ঐচ্ছিক)
        myForm.reset(); 
    })
    .catch(error => {
        console.error('সমস্যা হয়েছে:', error);
        alert('ত্রুটি: ডেটা পাঠানো যায়নি।');
    });
});
