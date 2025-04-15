const ColorTable = {
    Easy: 'green',
    Medium: 'orange',
    Hard: 'red',
    '数论': 'blue'
};


const GetTagColor = (tag : string) => {
    return ColorTable[tag as keyof typeof ColorTable];
}

export default GetTagColor;