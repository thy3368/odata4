import axios from 'axios';
import { Order, OrderItem, OrderStatus } from '../types/Order';

const BASE_URL = '/odata/Orders';

export class OrderService {
    static async getOrders(): Promise<Order[]> {
        const response = await axios.get(BASE_URL);
        return response.data.value;
    }

    static async getOrder(id: number): Promise<Order> {
        const response = await axios.get(`${BASE_URL}(${id})`);
        return response.data;
    }

    static async createOrder(order: Omit<Order, 'id'>): Promise<Order> {
        // 确保数据格式正确，包装在一个对象中
        const payload = {
            value: {
                "@odata.type": "#OData.Demo.Order",
                OrderNumber: order.orderNumber,
                Status: order.status,
                CustomerName: order.customerName,
                CustomerEmail: order.customerEmail || null,
                CustomerPhone: order.customerPhone || null,
                Notes: order.notes || null,
                Items: order.items.map(item => ({
                    "@odata.type": "#OData.Demo.OrderItem",
                    ProductCode: item.productCode,
                    ProductName: item.productName,
                    UnitPrice: Number(item.unitPrice),
                    Quantity: Number(item.quantity),
                    Notes: item.notes || null
                }))
            }
        };

        const response = await axios.post(BASE_URL, payload, {
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
                'OData-Version': '4.0'
            }
        });
        return response.data;
    }

    static async updateOrder(id: number, order: Partial<Order>): Promise<void> {
        // 同样包装更新数据
        const payload = {
            value: {
                "@odata.type": "#OData.Demo.Order",
                ...Object.entries(order).reduce((acc, [key, value]) => ({
                    ...acc,
                    [key.charAt(0).toUpperCase() + key.slice(1)]: value
                }), {})
            }
        };

        await axios.patch(`${BASE_URL}(${id})`, payload, {
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
                'OData-Version': '4.0'
            }
        });
    }

    static async deleteOrder(id: number): Promise<void> {
        await axios.delete(`${BASE_URL}(${id})`, {
            headers: {
                'Accept': 'application/json',
                'OData-Version': '4.0'
            }
        });
    }
} 