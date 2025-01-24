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
import { MinusCircleOutlined, PlusOutlined } from '@ant-design/icons';
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

    return (
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

            <Form.Item>
                <Space>
                    <Button type="primary" htmlType="submit" loading={loading}>
                        {mode === 'create' ? '创建' : '更新'}订单
                    </Button>
                    <Button onClick={() => navigate('/orders')}>
                        取消
                    </Button>
                </Space>
            </Form.Item>
        </Form>
    );
}; 