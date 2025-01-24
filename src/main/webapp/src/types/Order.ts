export enum OrderStatus {
    DRAFT = 'DRAFT',
    CONFIRMED = 'CONFIRMED',
    SHIPPED = 'SHIPPED',
    DELIVERED = 'DELIVERED',
    CANCELLED = 'CANCELLED'
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