import { request } from 'umi';
import type { Order, User, Product, TableParams } from '@/types/models';

const BASE_URL = '/api';

export async function getOrders(params?: TableParams) {
  return request<{ data: Order[]; total: number }>(`${BASE_URL}/orders`, {
    method: 'GET',
    params,
  });
}

export async function getOrder(id: number) {
  return request<Order>(`${BASE_URL}/orders/${id}`);
}

export async function createOrder(data: Partial<Order>) {
  return request<Order>(`${BASE_URL}/orders`, {
    method: 'POST',
    data,
  });
}

export async function updateOrder(id: number, data: Partial<Order>) {
  return request<Order>(`${BASE_URL}/orders/${id}`, {
    method: 'PUT',
    data,
  });
}

export async function deleteOrder(id: number) {
  return request<void>(`${BASE_URL}/orders/${id}`, {
    method: 'DELETE',
  });
}

export async function getUsers(params?: TableParams) {
  return request<{ data: User[]; total: number }>(`${BASE_URL}/users`, {
    method: 'GET',
    params,
  });
}

export async function getProducts(params?: TableParams) {
  return request<{ data: Product[]; total: number }>(`${BASE_URL}/products`, {
    method: 'GET',
    params,
  });
}

// OData API
export function buildODataUrl(entitySet: string, options?: {
  select?: string[];
  expand?: string[];
  filter?: string;
  orderby?: string;
  top?: number;
  skip?: number;
}) {
  let url = `/odata/${entitySet}`;
  const params: string[] = [];

  if (options?.select?.length) {
    params.push(`$select=${options.select.join(',')}`);
  }
  if (options?.expand?.length) {
    params.push(`$expand=${options.expand.join(',')}`);
  }
  if (options?.filter) {
    params.push(`$filter=${options.filter}`);
  }
  if (options?.orderby) {
    params.push(`$orderby=${options.orderby}`);
  }
  if (options?.top) {
    params.push(`$top=${options.top}`);
  }
  if (options?.skip) {
    params.push(`$skip=${options.skip}`);
  }

  if (params.length) {
    url += '?' + params.join('&');
  }

  return url;
}

export async function getODataOrders(options?: {
  select?: string[];
  expand?: string[];
  filter?: string;
  orderby?: string;
  top?: number;
  skip?: number;
}) {
  return request(buildODataUrl('Orders', options));
} 