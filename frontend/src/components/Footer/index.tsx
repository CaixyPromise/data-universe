import { GithubOutlined } from '@ant-design/icons';
import { DefaultFooter } from '@ant-design/pro-components';
import '@umijs/max';
import React from 'react';
const Footer: React.FC = () => {
  const defaultMessage = 'SOVO小飞象工作室-开发部';
  const currentYear = new Date().getFullYear();
  return (
    <DefaultFooter
      style={{
        background: 'none',
      }}
      copyright={`${currentYear} ${defaultMessage}`}
      links={[
        {
          key: '数据万象',
          title: '数据万象',
          href: 'https://www.github.com/CaixyPromise',
          blankTarget: true,
        },
        {
          key: 'github',
          title: <GithubOutlined />,
          href: 'https://www.github.com/CaixyPromise',
          blankTarget: true,
        },
        {
          key: '数据万象',
          title: '数据万象-计算机设计大赛参赛作品',
          href: 'https://www.github.com/CaixyPromise',
          blankTarget: true,
        },
      ]}
    />
  );
};
export default Footer;
