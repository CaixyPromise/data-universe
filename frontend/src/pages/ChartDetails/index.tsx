import {PageContainer} from "@ant-design/pro-components";
import {useLastPathname} from "@/hooks/useLastPathname";
import React, {useEffect, useState} from "react";
import {getChartResultUsingGet} from "@/services/backend/chartController";
import {Button, Card, Descriptions, Divider, message, Typography} from "antd";
import {Helmet, useNavigate} from "@umijs/max";
import ReactECharts from "echarts-for-react";
import Markdown from "react-markdown";
import html2canvas from "html2canvas";
import {jsPDF} from 'jspdf';
import dayjs from "dayjs";

const Index = () =>
{
    const { Title, Paragraph, Text, Link } = Typography;
    const { lastPath: id } = useLastPathname();
    const [ chartData, setChartData ] = useState<API.Chart>({});
    const [ loading, setLoading ] = useState<boolean>(false);
    const navigate = useNavigate();

    if (id === undefined)
    {
        navigate("/")
        return null;
    }

    const fetchChartsDetails = async () =>
    {
        try
        {
            setLoading(true)
            const { data, code } = await getChartResultUsingGet({ chartId: id });
            if (code === 0)
            {
                const chartOption = JSON.parse(data.genChart ?? '{}');
                chartOption.title = undefined;
                data.genChart = JSON.stringify(chartOption);
                console.log(data);
                setChartData(data);
            }
        }
        catch (e: any)
        {
            message.error("获取失败")
            navigate("/")
        }
        finally
        {
            setLoading(false);
        }
    }


    useEffect(() =>
    {
        fetchChartsDetails();
    }, [])
    const exportPDF = async () =>
    {
        const input = document.querySelector('.ant-pro-page-container');
        if (input)
        { // 确保元素存在
            const canvas = await html2canvas(input, {
                scale: 2, // 增加渲染质量
                useCORS: true // 允许加载跨域图片（如果有）
            });
            const imgData = canvas.toDataURL('image/png');
            const pdf = new jsPDF({
                orientation: "landscape",
                unit: "pt",
                format: [ canvas.width, canvas.height ]
            });
            pdf.addImage(imgData, 'PNG', 0, 0, canvas.width, canvas.height);
            pdf.save(`${chartData.name}-${dayjs(chartData.createTime).format('YYYY-MM-DD HH:mm:ss')}-导出分析报告.pdf`);
        }
        else
        {
            message.error('无法找到元素');
        }
    };

    return <>
        <Helmet>
            <title>
                {chartData.name}-分析详情
            </title>
        </Helmet>
        <PageContainer
        title={`分析详情-${chartData.name}`}
        loading={loading}
    >
        <Card title={"分析结果"}
              bordered={false}
              extra={<Button onClick={exportPDF}>导出PDF</Button>}
        >
            <Card
                title={"分析信息"}
                bordered={false}
                style={{
                    marginBottom: "8px"
                }}
            >
                <Descriptions column={3}>
                    <Descriptions.Item label={"图表名称"}>{chartData.name}</Descriptions.Item>
                    <Descriptions.Item label={"分析目标"}>{chartData.goal}</Descriptions.Item>
                    <Descriptions.Item label={"图表类型"}>{chartData.chartType}</Descriptions.Item>
                    <Descriptions.Item label={"创建时间"}>{dayjs(chartData.createTime).format(
                        "YYYY-MM-DD HH:mm:ss")}</Descriptions.Item>
                    <Descriptions.Item label={"更新时间"}>{dayjs(chartData.updateTime).format(
                        "YYYY-MM-DD HH:mm:ss")}</Descriptions.Item>
                    <Descriptions.Item label={"生成状态"}>{chartData.status}</Descriptions.Item>
                </Descriptions>
            </Card>

            <Card title="生成结果" bordered={false}>
                <Typography>
                    <Title>{chartData.name}-分析结果</Title>
                    <Paragraph>
                        <Markdown>{chartData.genResult}</Markdown>
                    </Paragraph>
                </Typography>
                <Divider/>
                <Title>可视化分析</Title>
                {chartData.genChart ? (
                    <ReactECharts option={JSON.parse(chartData.genChart)}/>
                ) : (
                    <div>正在加载图表数据...</div>
                )}
            </Card>
        </Card>
    </PageContainer>
    </>
}

export default Index;
