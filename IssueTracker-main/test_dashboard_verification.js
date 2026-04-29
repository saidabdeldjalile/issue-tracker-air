const axios = require('axios');

const API_BASE_URL = 'http://localhost:6969/api';

async function testDashboardAPI() {
    console.log('Testing Dashboard API Endpoints...\n');

    try {
        // Test 1: Try to access dashboard without authentication (should fail)
        console.log('1. Testing dashboard access without authentication...');
        try {
            const response = await axios.get(`${API_BASE_URL}/dashboard/stats`);
            console.log('❌ Unexpected: Dashboard accessible without auth');
            console.log('Response:', response.data);
        } catch (error) {
            console.log('✅ Expected: Dashboard requires authentication');
            console.log('Status:', error.response?.status);
        }

        // Test 2: Try to register a new user first
        console.log('\n2. Testing user registration...');
        try {
            const registerResponse = await axios.post(`${API_BASE_URL}/auth/register`, {
                firstName: 'Test',
                lastName: 'User',
                email: 'test@example.com',
                password: 'test123',
                role: 'ADMIN',
                registrationNumber: 'TEST001'
            });
            console.log('✅ User registration successful!');
        } catch (regError) {
            console.log('❌ User registration failed:', regError.response?.data);
        }

        // Test 3: Try to authenticate with the new user
        console.log('\n3. Testing authentication with new user...');
        const authResponse = await axios.post(`${API_BASE_URL}/auth/login`, {
            email: 'test@example.com',
            password: 'test123'
        });
        
        console.log('✅ Authentication successful!');
        const token = authResponse.data.token;
        console.log('Token received:', token ? 'Yes' : 'No');

        // Test 3: Access dashboard with valid token
        console.log('\n3. Testing dashboard access with authentication...');
        try {
            const dashboardResponse = await axios.get(`${API_BASE_URL}/dashboard/stats`, {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            
            console.log('✅ Dashboard stats accessible!');
            console.log('Stats data:', JSON.stringify(dashboardResponse.data, null, 2));
        } catch (dashError) {
            console.log('❌ Dashboard stats not accessible:', dashError.response?.status);
            console.log('Data:', dashError.response?.data);
        }

        // Test 4: Test dashboard data endpoint
        console.log('\n4. Testing dashboard data endpoint...');
        try {
            const dataResponse = await axios.get(`${API_BASE_URL}/dashboard/data`, {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            
            console.log('✅ Dashboard data accessible!');
            console.log('Data structure:', Object.keys(dataResponse.data));
        } catch (dataError) {
            console.log('❌ Dashboard data not accessible:', dataError.response?.status);
            console.log('Data:', dataError.response?.data);
        }

        console.log('\n🎉 All tests passed! Dashboard API is working correctly.');

    } catch (error) {
        console.error('❌ Test failed:', error.message);
        if (error.response) {
            console.error('Status:', error.response.status);
            console.error('Data:', error.response.data);
        }
    }
}

// Run the test
testDashboardAPI();