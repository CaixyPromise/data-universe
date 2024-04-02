import { ProLayoutProps } from '@ant-design/pro-components';

/**
 * @name
 */
const Settings: ProLayoutProps & {
  pwa?: boolean;
  logo?: string;
} = {
  "navTheme": "light",
  "colorPrimary": "#2F54EB",
  "layout": "top",
  "contentWidth": "Fixed",
  "fixedHeader": false,
  "fixSiderbar": true,
  "pwa": true,
  "title": "数据分析平台",
  "logo": "./logo.svg",
  "token": {},
  "splitMenus": false,
  "menuRender": false,
};

export default Settings;
