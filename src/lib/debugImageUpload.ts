// Debugging utility for testing vehicle image upload
// Add this temporarily to your page.tsx for testing

export const debugImageUpload = () => {
  console.log('🔍 Starting Image Upload Debug...');
  
  // Create file input
  const input = document.createElement('input');
  input.type = 'file';
  input.accept = 'image/*';
  
  input.onchange = async (e) => {
    const file = (e.target as HTMLInputElement).files?.[0];
    
    if (!file) {
      console.error('❌ No file selected');
      return;
    }

    console.log('📁 File Information:');
    console.log('  - Name:', file.name);
    console.log('  - Type:', file.type);
    console.log('  - Size:', (file.size / 1024).toFixed(2), 'KB');
    console.log('  - Last Modified:', new Date(file.lastModified));
    console.log('  - Is File object:', file instanceof File);

    const vehicleData = {
      model: "Debug Test Vehicle",
      color: "Red",
      vin: "DEBUG" + Date.now(),
      licensePlate: "DBG-" + Date.now(),
      year: 2023,
      registrationDate: "2023-11-06"
    };

    console.log('🚗 Vehicle Data:', vehicleData);

    // Create FormData
    const formData = new FormData();
    formData.append('vehicle', JSON.stringify(vehicleData));
    formData.append('image', file);

    console.log('📦 FormData Contents:');
    for (let pair of formData.entries()) {
      console.log('  -', pair[0] + ':', typeof pair[1] === 'string' ? pair[1] : pair[1]);
    }

    // Check token
    const token = localStorage.getItem('auth_token');
    console.log('🔐 Auth Token:', token ? '✅ Present' : '❌ Missing');
    if (token) {
      console.log('  - Token preview:', token.substring(0, 20) + '...');
    }

    // Test API call
    console.log('🌐 Making API Call...');
    console.log('⚠️  NOTE: NOT setting Content-Type header - browser will set it automatically with boundary');
    try {
      const response = await fetch('http://localhost:8080/api/vehicles/customer/my-vehicles/with-image', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`
          // CRITICAL: DON'T set Content-Type - let browser add boundary automatically
          // Setting 'Content-Type': 'multipart/form-data' will break the request!
        },
        body: formData
      });

      console.log('📡 Response Status:', response.status, response.statusText);
      
      if (response.ok) {
        const data = await response.json();
        console.log('✅ SUCCESS!', data);
        alert('✅ Image uploaded successfully!\n\nCheck console for details.');
      } else {
        const errorText = await response.text();
        console.error('❌ ERROR Response:', errorText);
        alert('❌ Upload failed!\n\nStatus: ' + response.status + '\nCheck console for details.');
      }
    } catch (error) {
      console.error('❌ NETWORK ERROR:', error);
      alert('❌ Network error!\n\nCheck if backend is running on port 8080.\nCheck console for details.');
    }
  };

  // Trigger file selection
  input.click();
  console.log('📂 File picker opened...');
};

// To use: Add this to your vehicle page component and call it:
// <button onClick={debugImageUpload}>🐛 Debug Image Upload</button>
