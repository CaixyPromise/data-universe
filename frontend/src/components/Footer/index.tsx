import { GithubOutlined } from '@ant-design/icons';
import { DefaultFooter } from '@ant-design/pro-components';
import '@umijs/max';
import React from 'react';
const Footer: React.FC = () => {
  const defaultMessage = 'SOVO小飞象工作室';
  const currentYear = new Date().getFullYear();
  return (
    <DefaultFooter
      style={{
        background: 'none',
      }}
      copyright={`${currentYear} ${defaultMessage}`}
      links={[
        {
          key: '数据分析平台',
          title: '数据分析平台',
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
          key: '数据分析平台',
          title: '数据分析平台',
          href: 'https://www.github.com/CaixyPromise',
          blankTarget: true,
        },
      ]}
    />
  );
};
export default Footer;
