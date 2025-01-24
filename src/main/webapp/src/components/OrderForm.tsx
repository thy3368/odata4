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

interface OrderFormProps {
    mode: 'create' | 'edit';
}

export const OrderForm: React.FC<OrderFormProps> = ({ mode }) => {
    const [form] = Form.useForm();
    const navigate = useNavigate();
    const { id } = useParams();
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
            message.error('Failed to load order');
            navigate('/orders');
        } finally {
            setLoading(false);
        }
    };

    const onFinish = async (values: any) => {
        try {
            setLoading(true);
            if (mode === 'create') {
                await OrderService.createOrder(values);
                message.success('Order created successfully');
            } else if (id) {
                await OrderService.updateOrder(parseInt(id), values);
                message.success('Order updated successfully');
            }
            navigate('/orders');
        } catch (error) {
            message.error('Failed to save order');
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
            <Form.Item
                name="orderNumber"
                label="Order Number"
                rules={[{ required: true, message: 'Please input order number' }]}
            >
                <Input />
            </Form.Item>

            <Form.Item
                name="status"
                label="Status"
                rules={[{ required: true, message: 'Please select status' }]}
            >
                <Select>
                    {Object.values(OrderStatus).map(status => (
                        <Select.Option key={status} value={status}>
                            {status}
                        </Select.Option>
                    ))}
                </Select>
            </Form.Item>

            <Form.Item
                name="customerName"
                label="Customer Name"
                rules={[{ required: true, message: 'Please input customer name' }]}
            >
                <Input />
            </Form.Item>

            <Form.Item
                name="customerEmail"
                label="Customer Email"
                rules={[{ type: 'email', message: 'Please input valid email' }]}
            >
                <Input />
            </Form.Item>

            <Form.Item
                name="customerPhone"
                label="Customer Phone"
            >
                <Input />
            </Form.Item>

            <Form.List name="items">
                {(fields, { add, remove }) => (
                    <>
                        {fields.map(({ key, name, ...restField }) => (
                            <Space key={key} style={{ display: 'flex', marginBottom: 8 }} align="baseline">
                                <Form.Item
                                    {...restField}
                                    name={[name, 'productCode']}
                                    rules={[{ required: true, message: 'Missing product code' }]}
                                >
                                    <Input placeholder="Product Code" />
                                </Form.Item>
                                <Form.Item
                                    {...restField}
                                    name={[name, 'productName']}
                                    rules={[{ required: true, message: 'Missing product name' }]}
                                >
                                    <Input placeholder="Product Name" />
                                </Form.Item>
                                <Form.Item
                                    {...restField}
                                    name={[name, 'unitPrice']}
                                    rules={[{ required: true, message: 'Missing unit price' }]}
                                >
                                    <InputNumber
                                        placeholder="Unit Price"
                                        min={0}
                                        precision={2}
                                        style={{ width: '150px' }}
                                    />
                                </Form.Item>
                                <Form.Item
                                    {...restField}
                                    name={[name, 'quantity']}
                                    rules={[{ required: true, message: 'Missing quantity' }]}
                                >
                                    <InputNumber
                                        placeholder="Quantity"
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
                                Add Item
                            </Button>
                        </Form.Item>
                    </>
                )}
            </Form.List>

            <Form.Item name="notes" label="Notes">
                <Input.TextArea />
            </Form.Item>

            <Form.Item>
                <Space>
                    <Button type="primary" htmlType="submit" loading={loading}>
                        {mode === 'create' ? 'Create' : 'Update'} Order
                    </Button>
                    <Button onClick={() => navigate('/orders')}>
                        Cancel
                    </Button>
                </Space>
            </Form.Item>
        </Form>
    );
}; 