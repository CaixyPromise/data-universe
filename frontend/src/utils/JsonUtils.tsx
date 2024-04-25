
class JsonUtils
{

    public static safeJsonParse = (jsonStr: string) =>
    {
        try {
            return JSON.parse(jsonStr || "{}");
        }
        catch (e: any)
        {
            return {}
        }
    }

}

export default JsonUtils;
