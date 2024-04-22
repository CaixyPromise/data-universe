import {ProLayoutProps} from '@ant-design/pro-components';

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
    // "logo": "https://gw.alipayobjects.com/zos/rmsportal/KDpgvguMpGfqaHPjicRK.svg",
    "token": {},
    "splitMenus": false,
    "menuRender": false,

}
export default Settings;
