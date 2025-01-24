import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { MainLayout } from './components/layout/MainLayout';
import { OrderList } from './components/OrderList';
import { OrderForm } from './components/OrderForm';

const App: React.FC = () => {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<MainLayout />}>
                    <Route index element={<Navigate to="/orders" replace />} />
                    <Route path="orders" element={<OrderList />} />
                    <Route path="orders/new" element={<OrderForm />} />
                    <Route path="orders/:id/edit" element={<OrderForm />} />
                </Route>
            </Routes>
        </Router>
    );
};

export default App; 