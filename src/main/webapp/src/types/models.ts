export interface Order {
  id: number;
  orderNumber: string;
  orderDate: string;
  totalAmount: number;
  status: OrderStatus;
  items: OrderItem[];
  user: User;
}

export interface OrderItem {
  id: number;
  quantity: number;
  unitPrice: number;
  order: Order;
  product: Product;
}

export interface User {
  id: number;
  username: string;
  email: string;
  phone: string;
  address: string;
  orders?: Order[];
}

export interface Product {
  id: number;
  name: string;
  description: string;
  price: number;
  stock: number;
  imageUrl: string;
}

export enum OrderStatus {
  PENDING = 'PENDING',
  PROCESSING = 'PROCESSING',
  COMPLETED = 'COMPLETED',
  CANCELLED = 'CANCELLED'
}

export interface PaginationParams {
  current?: number;
  pageSize?: number;
  total?: number;
}

export interface SortParams {
  field?: string;
  order?: 'ascend' | 'descend';
}

export interface FilterParams {
  [key: string]: any;
}

export interface TableParams {
  pagination?: PaginationParams;
  sort?: SortParams;
  filters?: FilterParams;
} 