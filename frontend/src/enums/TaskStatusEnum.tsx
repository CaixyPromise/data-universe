class TaskStatusEnum
{
    private static readonly taskStatusEnum = {
        succeed: "成功",
        failed: "失败",
        waiting: "等待中",
        running: "运行中",
    }

    public static getTextByValue(value: string): string {
        // 直接通过访问对象属性来获取值
        const text = TaskStatusEnum.taskStatusEnum[value];
        return text ? text : "未知";
    }
}

export default TaskStatusEnum;
