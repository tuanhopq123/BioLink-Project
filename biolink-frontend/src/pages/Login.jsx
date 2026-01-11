import { useState } from 'react';
import axiosClient from '../api/axiosClient';

const Login = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const handleLogin = async (e) => {
    e.preventDefault();
    try {
      const response = await axiosClient.post('/auth/login', {
        email,
        password
      });

      console.log("Kết quả:", response);
      // Lưu Access Token
      localStorage.setItem('accessToken', response.accessToken);
      
      alert("✅ Đăng nhập thành công!");
      
    } catch (err) {
      console.error(err);
      // Xử lý lỗi an toàn hơn nếu server chưa trả về đúng format
      const message = err.response?.data?.message || "Lỗi kết nối hoặc sai tài khoản";
      alert("❌ Đăng nhập thất bại: " + message);
    }
  };

  return (
    <div className="flex min-h-screen items-center justify-center bg-gray-100">
      <div className="w-full max-w-md p-8 bg-white rounded-lg shadow-md">
        <h2 className="text-2xl font-bold text-center mb-6 text-gray-800">Đăng Nhập SaaS</h2>
        
        <form onSubmit={handleLogin} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700">Email</label>
            <input
              type="email"
              className="w-full p-2 border rounded mt-1"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="boss@gmail.com"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700">Mật khẩu</label>
            <input
              type="password"
              className="w-full p-2 border rounded mt-1"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
          </div>
          <button type="submit" className="w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700 font-bold">
            Đăng Nhập
          </button>
        </form>
      </div>
    </div>
  );
};

export default Login;