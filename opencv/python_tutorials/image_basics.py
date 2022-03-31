import sys

import cv2 as cv

cv.samples.addSamplesDataSearchPath("/Users/collin/Files/Developer/[Demo]/opencv/samples/data")

# 读图
img = cv.imread(cv.samples.findFile("blox.jpg"))

# 图片为空
if img is None:
    sys.exit("Could not read the image.")

# 展示图片
cv.imshow("Display window", img)
k = cv.waitKey(0)  # 直到键盘被按下, 0 = forever

# 保存
if k == ord("s"):
    cv.imwrite("starry_night.png", img)
