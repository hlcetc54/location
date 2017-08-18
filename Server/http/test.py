def normal(arr):
    arr_1 = arr[0:9]
    max_1 = max (arr_1)
    arr_1_ = []
    for i in arr_1:
        arr_1_.append (i / max_1)
    return arr_1_


lst = [2,3,2,6,3,3,5,2,5,2]

print(normal(lst))
