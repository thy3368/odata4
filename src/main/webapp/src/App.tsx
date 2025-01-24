import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { Layout } from 'antd';
import { OrderList } from './components/OrderList';
import { OrderForm } from './components/OrderForm';

const { Header, Content } = Layout;

const App: React.FC = () => {
  return (
    <Router>
      <Layout>
        <Header style={{ color: 'white', fontSize: '20px' }}>
          Order Management System
        </Header>
        <Content style={{ padding: '24px', minHeight: 'calc(100vh - 64px)' }}>
          <Routes>
            <Route path="/" element={<OrderList />} />
            <Route path="/orders" element={<OrderList />} />
            <Route path="/orders/new" element={<OrderForm mode="create" />} />
            <Route path="/orders/:id/edit" element={<OrderForm mode="edit" />} />
          </Routes>
        </Content>
      </Layout>
    </Router>
  );
};

export default App; 