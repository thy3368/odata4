import React from 'react';
import { Layout, Menu } from 'antd';
import { Link, Outlet, useLocation } from 'react-router-dom';
import { ShoppingCartOutlined, UnorderedListOutlined } from '@ant-design/icons';

const { Header, Content, Sider } = Layout;

export const MainLayout: React.FC = () => {
    const location = useLocation();

    const menuItems = [
        {
            key: '/orders',
            icon: <UnorderedListOutlined />,
            label: <Link to="/orders">订单列表</Link>,
        },
        {
            key: '/orders/new',
            icon: <ShoppingCartOutlined />,
            label: <Link to="/orders/new">新建订单</Link>,
        },
    ];

    return (
        <Layout style={{ minHeight: '100vh' }}>
            <Header style={{ 
                padding: 0, 
                background: '#fff',
                borderBottom: '1px solid #f0f0f0',
                display: 'flex',
                alignItems: 'center',
                paddingLeft: '24px'
            }}>
                <h1 style={{ margin: 0, fontSize: '18px' }}>订单管理系统</h1>
            </Header>
            <Layout>
                <Sider width={200} style={{ background: '#fff' }}>
                    <Menu
                        mode="inline"
                        selectedKeys={[location.pathname]}
                        style={{ height: '100%', borderRight: 0 }}
                        items={menuItems}
                    />
                </Sider>
                <Layout style={{ padding: '24px' }}>
                    <Content style={{
                        background: '#fff',
                        padding: 24,
                        margin: 0,
                        minHeight: 280,
                    }}>
                        <Outlet />
                    </Content>
                </Layout>
            </Layout>
        </Layout>
    );
}; 