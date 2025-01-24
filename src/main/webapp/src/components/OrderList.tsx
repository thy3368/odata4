import React, { useEffect, useState } from 'react';
import { Table, Button, Space, message, Form, Input, Select, Card, Row, Col } from 'antd';
import { OrderService } from '../services/OrderService';
import { Order, OrderStatus } from '../types/Order';
import { useNavigate } from 'react-router-dom';
import { SearchOutlined, ReloadOutlined } from '@ant-design/icons';

interface OrderSearchParams {
    orderNumber?: string;
    customerName?: string;
    status?: OrderStatus;
}

export const OrderList: React.FC = () => {
    const [orders, setOrders] = useState<Order[]>([]);
    const [loading, setLoading] = useState(false);
    const [searchForm] = Form.useForm();
    const navigate = useNavigate();

    const loadOrders = async (params?: OrderSearchParams) => {
        try {
            setLoading(true);
            const data = await OrderService.getOrders(params);
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

    const handleSearch = async (values: OrderSearchParams) => {
        await loadOrders(values);
    };

    const handleReset = () => {
        searchForm.resetFields();
        loadOrders();
    };

    const handleDelete = async (id: number) => {
        try {
            await OrderService.deleteOrder(id);
            message.success('Order deleted successfully');
            loadOrders(searchForm.getFieldsValue());
        } catch (error) {
            message.error('Failed to delete order');
        }
    };

    const columns = [
        {
            title: '订单编号',
            dataIndex: 'orderNumber',
            key: 'orderNumber',
        },
        {
            title: '客户名称',
            dataIndex: 'customerName',
            key: 'customerName',
        },
        {
            title: '订单状态',
            dataIndex: 'status',
            key: 'status',
        },
        {
            title: '订单金额',
            dataIndex: 'totalAmount',
            key: 'totalAmount',
            render: (amount: number | null | undefined) => {
                if (amount === null || amount === undefined) {
                    return '¥0.00';
                }
                return `¥${amount.toFixed(2)}`;
            },
        },
        {
            title: '操作',
            key: 'actions',
            render: (_: any, record: Order) => (
                <Space>
                    <Button onClick={() => navigate(`/orders/${record.id}`)}>
                        查看
                    </Button>
                    <Button onClick={() => navigate(`/orders/${record.id}/edit`)}>
                        编辑
                    </Button>
                    <Button danger onClick={() => handleDelete(record.id!)}>
                        删除
                    </Button>
                </Space>
            ),
        },
    ];

    return (
        <div>
            <Card style={{ marginBottom: 16 }}>
                <Form
                    form={searchForm}
                    onFinish={handleSearch}
                    layout="vertical"
                >
                    <Row gutter={16}>
                        <Col span={6}>
                            <Form.Item name="orderNumber" label="订单编号">
                                <Input placeholder="请输入订单编号" />
                            </Form.Item>
                        </Col>
                        <Col span={6}>
                            <Form.Item name="customerName" label="客户名称">
                                <Input placeholder="请输入客户名称" />
                            </Form.Item>
                        </Col>
                        <Col span={6}>
                            <Form.Item name="status" label="订单状态">
                                <Select 
                                    allowClear 
                                    placeholder="请选择订单状态"
                                >
                                    {Object.values(OrderStatus).map(status => (
                                        <Select.Option key={status} value={status}>
                                            {status}
                                        </Select.Option>
                                    ))}
                                </Select>
                            </Form.Item>
                        </Col>
                        <Col span={6} style={{ display: 'flex', alignItems: 'flex-end' }}>
                            <Space>
                                <Button 
                                    type="primary" 
                                    htmlType="submit" 
                                    icon={<SearchOutlined />}
                                >
                                    查询
                                </Button>
                                <Button 
                                    onClick={handleReset}
                                    icon={<ReloadOutlined />}
                                >
                                    重置
                                </Button>
                            </Space>
                        </Col>
                    </Row>
                </Form>
            </Card>

            <Card>
                <div style={{ marginBottom: 16 }}>
                    <Button type="primary" onClick={() => navigate('/orders/new')}>
                        新建订单
                    </Button>
                </div>

                <Table
                    columns={columns}
                    dataSource={orders}
                    rowKey="id"
                    loading={loading}
                />
            </Card>
        </div>
    );
}; 