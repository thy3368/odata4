export enum OrderStatus {
    DRAFT = '草稿',
    PENDING = '待处理',
    PROCESSING = '处理中',
    COMPLETED = '已完成',
    CANCELLED = '已取消'
}

export interface OrderItem {
    id?: number;
    productCode: string;
    productName: string;
    unitPrice: number;
    quantity: number;
    subtotal: number;
    notes?: string;
}

export interface Order {
    id?: number;
    orderNumber: string;
    status: OrderStatus;
    customerName: string;
    customerEmail?: string;
    customerPhone?: string;
    totalAmount: number;
    notes?: string;
    items: OrderItem[];
} 