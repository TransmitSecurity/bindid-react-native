export class Utils {

    public static jsonObjectToArray(obj: any) {
        return this.objToArray(obj)
    }

    private static isObject(obj: any) {
        return typeof obj === 'object' && !Array.isArray(obj) && obj !== null;
    }

    private static objToArray(obj: any): any {
        return Object.keys(obj).map((key: any) => {
            return [
                key, this.isObject(obj[key]) ?
                    this.objToArray(obj[key]) :
                    obj[key]
            ];
        });
    }
    
}