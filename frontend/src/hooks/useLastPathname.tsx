import {useLocation, useResolvedPath} from "@@/exports";
import React, { useContext } from 'react';
import { RouteContext } from '@ant-design/pro-layout';
export const useLastPathname = () =>
{
    const location = useLocation();
    const { title } = useContext(RouteContext);
    const pathSegments = location.pathname.split('/');

    return {
        title: title,
        routeName: pathSegments[1],
        lastPath: pathSegments.pop()
    }
};