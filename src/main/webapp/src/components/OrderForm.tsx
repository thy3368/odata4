import React, { useEffect, useState } from 'react';
import {
    Form,
    Input,
    Select,
    Button,
    Space,
    InputNumber,
    message,
} from 'antd';
import { MinusCircleOutlined, PlusOutlined, LeftOutlined, RightOutlined, SaveOutlined, SearchOutlined, CheckCircleOutlined } from '@ant-design/icons';
import { Order, OrderStatus } from '../types/Order';
import { OrderService } from '../services/OrderService';
import { useNavigate, useParams } from 'react-router-dom';

export interface OrderFormProps {
    mode?: 'create' | 'edit';
}

export const OrderForm: React.FC<OrderFormProps> = ({ mode = 'create' }) => {
    const [form] = Form.useForm();
    const navigate = useNavigate();
    const { id } = useParams<{ id: string }>();
    const [loading, setLoading] = useState(false);
    const [currentId, setCurrentId] = useState<number | undefined>(id ? parseInt(id) : undefined);
    const [hasNext, setHasNext] = useState(false);
    const [hasPrev, setHasPrev] = useState(false);

    useEffect(() => {
        if (mode === 'edit' && id) {
            loadOrder(parseInt(id));
        }
    }, [mode, id]);

    const loadOrder = async (orderId: number) => {
        try {
            setLoading(true);
            const order = await OrderService.getOrder(orderId);
            form.setFieldsValue(order);
        } catch (error) {
            message.error('加载订单失败');
            navigate('/orders');
        } finally {
            setLoading(false);
        }
    };

    const onFinish = async (values: Partial<Order>) => {
        try {
            setLoading(true);
            if (mode === 'edit' && id) {
                await OrderService.updateOrder(parseInt(id), values);
                message.success('订单更新成功');
            } else {
                await OrderService.createOrder(values as Omit<Order, 'id'>);
                message.success('订单创建成功');
            }
            navigate('/orders');
        } catch (error) {
            message.error(mode === 'create' ? '创建订单失败' : '更新订单失败');
        } finally {
            setLoading(false);
        }
    };

    const checkNavigation = async (currentOrderId: number) => {
        try {
            const orders = await OrderService.getOrders();
            const currentIndex = orders.findIndex(order => order.id === currentOrderId);
            setHasPrev(currentIndex > 0);
            setHasNext(currentIndex < orders.length - 1 && currentIndex !== -1);
        } catch (error) {
            console.error('Failed to check navigation:', error);
        }
    };

    useEffect(() => {
        if (currentId) {
            checkNavigation(currentId);
        }
    }, [currentId]);

    const navigateToOrder = async (direction: 'prev' | 'next') => {
        try {
            const orders = await OrderService.getOrders();
            const currentIndex = orders.findIndex(order => order.id === currentId);
            if (currentIndex === -1) return;

            const targetIndex = direction === 'prev' ? currentIndex - 1 : currentIndex + 1;
            if (targetIndex >= 0 && targetIndex < orders.length) {
                const targetId = orders[targetIndex].id;
                if (targetId) {
                    navigate(`/orders/${targetId}/edit`);
                    setCurrentId(targetId);
                }
            }
        } catch (error) {
            message.error('导航失败');
        }
    };

    const handleNew = () => {
        form.resetFields();
        navigate('/orders/new');
    };

    const handleSearch = () => {
        navigate('/orders');
    };

    const handleAudit = async () => {
        try {
            if (!currentId) {
                message.error('无法审核未保存的订单');
                return;
            }

            setLoading(true);
            await OrderService.updateOrder(currentId, {
                status: OrderStatus.PENDING,
            });
            message.success('订单审核成功');
            
            await loadOrder(currentId);
        } catch (error) {
            message.error('订单审核失败');
        } finally {
            setLoading(false);
        }
    };

    const canAudit = form.getFieldValue('status') === OrderStatus.DRAFT;

    return (
        <div>
            <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between' }}>
                <Space size="middle">
                    <Button 
                        icon={<PlusOutlined />} 
                        onClick={handleNew}
                    >
                        新建
                    </Button>
                    <Button 
                        type="primary"
                        icon={<SaveOutlined />}
                        onClick={() => form.submit()}
                        loading={loading}
                    >
                        保存
                    </Button>
                    <Button
                        type="primary"
                        icon={<CheckCircleOutlined />}
                        onClick={handleAudit}
                        disabled={!canAudit || mode === 'create'}
                        loading={loading}
                    >
                        审核
                    </Button>
                </Space>
                <Space size="middle">
                    <Button.Group>
                        <Button 
                            icon={<LeftOutlined />}
                            disabled={!hasPrev}
                            onClick={() => navigateToOrder('prev')}
                        >
                            前一条
                        </Button>
                        <Button 
                            icon={<RightOutlined />}
                            disabled={!hasNext}
                            onClick={() => navigateToOrder('next')}
                        >
                            后一条
                        </Button>
                    </Button.Group>
                    <Button 
                        icon={<SearchOutlined />}
                        onClick={handleSearch}
                    >
                        查询
                    </Button>
                </Space>
            </div>

            <Form
                form={form}
                layout="vertical"
                onFinish={onFinish}
                initialValues={{
                    status: OrderStatus.DRAFT,
                    items: [],
                }}
            >
                <h2>{mode === 'create' ? '新建订单' : '编辑订单'}</h2>

                <Form.Item
                    name="orderNumber"
                    label="订单编号"
                    rules={[{ required: true, message: '请输入订单编号' }]}
                >
                    <Input placeholder="请输入订单编号" />
                </Form.Item>

                <Form.Item
                    name="status"
                    label="订单状态"
                    rules={[{ required: true, message: '请选择订单状态' }]}
                >
                    <Select placeholder="请选择订单状态">
                        {Object.values(OrderStatus).map(status => (
                            <Select.Option key={status} value={status}>
                                {status}
                            </Select.Option>
                        ))}
                    </Select>
                </Form.Item>

                <Form.Item
                    name="customerName"
                    label="客户名称"
                    rules={[{ required: true, message: '请输入客户名称' }]}
                >
                    <Input placeholder="请输入客户名称" />
                </Form.Item>

                <Form.Item
                    name="customerEmail"
                    label="客户邮箱"
                    rules={[{ type: 'email', message: '请输入有效的邮箱地址' }]}
                >
                    <Input placeholder="请输入客户邮箱" />
                </Form.Item>

                <Form.Item
                    name="customerPhone"
                    label="客户电话"
                >
                    <Input placeholder="请输入客户电话" />
                </Form.Item>

                <Form.List name="items">
                    {(fields, { add, remove }) => (
                        <>
                            {fields.map(({ key, name, ...restField }) => (
                                <Space key={key} style={{ display: 'flex', marginBottom: 8 }} align="baseline">
                                    <Form.Item
                                        {...restField}
                                        name={[name, 'productCode']}
                                        rules={[{ required: true, message: '请输入商品编码' }]}
                                    >
                                        <Input placeholder="商品编码" />
                                    </Form.Item>
                                    <Form.Item
                                        {...restField}
                                        name={[name, 'productName']}
                                        rules={[{ required: true, message: '请输入商品名称' }]}
                                    >
                                        <Input placeholder="商品名称" />
                                    </Form.Item>
                                    <Form.Item
                                        {...restField}
                                        name={[name, 'unitPrice']}
                                        rules={[{ required: true, message: '请输入单价' }]}
                                    >
                                        <InputNumber
                                            placeholder="单价"
                                            min={0}
                                            precision={2}
                                            style={{ width: '150px' }}
                                            prefix="¥"
                                        />
                                    </Form.Item>
                                    <Form.Item
                                        {...restField}
                                        name={[name, 'quantity']}
                                        rules={[{ required: true, message: '请输入数量' }]}
                                    >
                                        <InputNumber
                                            placeholder="数量"
                                            min={1}
                                            style={{ width: '100px' }}
                                        />
                                    </Form.Item>
                                    <MinusCircleOutlined onClick={() => remove(name)} />
                                </Space>
                            ))}
                            <Form.Item>
                                <Button
                                    type="dashed"
                                    onClick={() => add()}
                                    block
                                    icon={<PlusOutlined />}
                                >
                                    添加商品
                                </Button>
                            </Form.Item>
                        </>
                    )}
                </Form.List>

                <Form.Item name="notes" label="备注">
                    <Input.TextArea placeholder="请输入备注信息" />
                </Form.Item>
            </Form>
        </div>
    );
}; 