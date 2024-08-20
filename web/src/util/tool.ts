interface Category {
    id: string;
    parent: string;
    name: string;
    sort: number;
}

interface Dictionary {
    [key: string]: ParentType;
}

interface ParentType {
    id: string;
    parent: string;
    name: string;
    sort: number;
    children: Array<Category>
}


export class Tool {
    /**
     * 空校验 null或""都返回true
     */
    public static isEmpty (obj: any) {
        if ((typeof obj === 'string')) {
            return !obj || obj.replace(/\s+/g, "") === ""
        } else {
            return (!obj || JSON.stringify(obj) === "{}" || obj.length === 0);
        }
    }

    /**
     * 非空校验
     */
    public static isNotEmpty (obj: any) {
        return !this.isEmpty(obj);
    }

    /**
     * 对象复制
     * @param obj
     */
    public static copy (obj: any) {
        if (Tool.isNotEmpty(obj)) {
            return JSON.parse(JSON.stringify(obj));
        }
    }



    // public static arrayToTree(array: Array<Category>) {
    //     const map:Dictionary = {};
    //     array.forEach(item => {
    //         if (item.parent === "0") {
    //             const newItem = Tool.copy(item);
    //             newItem.children = [];
    //             map[item.id] = newItem;
    //         }
    //     })
    //     array.forEach(item => {
    //         if (item.parent !== "0") {
    //             map[item.parent].children.push(item);
    //         }
    //     })
    //     const result:Array<ParentType> = [];
    //     for (const key in map) {
    //         result.push(map[key]);
    //     }
    //
    //     return result;
    // }


    public static arrayToTree(array: Array<Category>) {
        const map:Map<string, ParentType> = new Map<string, ParentType>();
        array.forEach(item => {
            if (item.parent === "0") {
                const newItem = Tool.copy(item);
                newItem.children = [];
                map.set(item.id, newItem);
            }
        })
        array.forEach(item => {
            if (item.parent !== "0") {
                const parentItem = map.get(item.parent);
                parentItem!.children.push(item);
            }
        })
        const result:Array<ParentType> = [];
        map.forEach((value, key) => {
            result.push(value);
        })

        return result;
    }


    public static arrayToTree2 (array: any, parentId: number) {
        if (Tool.isEmpty(array)) {
            return [];
        }

        const result = [];
        for (let i = 0; i < array.length; i++) {
            const c = array[i];
            // console.log(Number(c.parent), Number(parentId));
            if (Number(c.parent) === Number(parentId)) {
                result.push(c);

                // 递归查看当前节点对应的子节点
                const children = Tool.arrayToTree2(array, c.id);
                if (Tool.isNotEmpty(children)) {
                    c.children = children;
                }
            }
        }
        return result;
    }


    public static arrayToTree3(array: Array<any>) {
        const map:Map<string, any> = new Map();
        array.forEach(item => {
            map.set(item.id, item);
        })

        const parentToChildItemMap:Map<string, any> = new Map();
        array.forEach(item => {
            if (!parentToChildItemMap.get(item.parent)) {
                parentToChildItemMap.set(item.parent, []);
            }
            const childrenArray = parentToChildItemMap.get(item.parent);
            childrenArray.push(item);
        })



        parentToChildItemMap.forEach((value, key) => {
            if (key !== "0") {
                map.get(key).children = value;
            }
        })

        const result:Array<any> = [];

        map.forEach(item => {
            if (item.parent === "0") {
                result.push(item);
            }
        })

        return result;
    }

    /**
   * 随机生成[len]长度的[radix]进制数
   * @param len
   * @param radix 默认62
   * @returns {string}
   */
  public static uuid (len: number, radix = 62) {
    const chars = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'.split('');
    const uuid = [];
    radix = radix || chars.length;

    for (let i = 0; i < len; i++) {
      uuid[i] = chars[0 | Math.random() * radix];
    }

    return uuid.join('');
  }
}
