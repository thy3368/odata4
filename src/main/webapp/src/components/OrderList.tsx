import React, { useEffect, useState } from 'react';
import { Table, Button, Space, message } from 'antd';
import { OrderService } from '../services/OrderService';
import { Order } from '../types/Order';
import { useNavigate } from 'react-router-dom';

export const OrderList: React.FC = () => {
    const [orders, setOrders] = useState<Order[]>([]);
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    const loadOrders = async () => {
        try {
            setLoading(true);
            const data = await OrderService.getOrders();
            setOrders(data);
        } catch (error) {
            message.error('Failed to load orders');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        loadOrders();
    }, []);

    const handleDelete = async (id: number) => {
        try {
            await OrderService.deleteOrder(id);
            message.success('Order deleted successfully');
            loadOrders();
        } catch (error) {
            message.error('Failed to delete order');
        }
    };

    const columns = [
        {
            title: 'Order Number',
            dataIndex: 'orderNumber',
            key: 'orderNumber',
        },
        {
            title: 'Customer',
            dataIndex: 'customerName',
            key: 'customerName',
        },
        {
            title: 'Status',
            dataIndex: 'status',
            key: 'status',
        },
        {
            title: 'Total Amount',
            dataIndex: 'totalAmount',
            key: 'totalAmount',
            render: (amount: number | null | undefined) => {
                if (amount === null || amount === undefined) {
                    return '$0.00';
                }
                return `$${amount.toFixed(2)}`;
            },
        },
        {
            title: 'Actions',
            key: 'actions',
            render: (_: any, record: Order) => (
                <Space>
                    <Button onClick={() => navigate(`/orders/${record.id}`)}>
                        View
                    </Button>
                    <Button onClick={() => navigate(`/orders/${record.id}/edit`)}>
                        Edit
                    </Button>
                    <Button danger onClick={() => handleDelete(record.id!)}>
                        Delete
                    </Button>
                </Space>
            ),
        },
    ];

    return (
        <div>
            <div style={{ marginBottom: 16 }}>
                <Button type="primary" onClick={() => navigate('/orders/new')}>
                    Create New Order
                </Button>
            </div>
            <Table
                columns={columns}
                dataSource={orders}
                rowKey="id"
                loading={loading}
            />
        </div>
    );
}; 