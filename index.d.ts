declare module "cordova-plugin-color-picker" {
    export interface ColorPicker {
        isAvailable(
            successCb: (available: boolean) => void, 
            errorCb?: (error: any) => void,
        );
        showDialog(
            options: {
                color?: string,
                withAlpha?: boolean,
                withProgress?: boolean,
            }, 
            successCb: (
                result: {
                    color: string,
                    dismissed: boolean,
                }) => void, 
            errorCb?: (error: any) => void
        );
    }
}