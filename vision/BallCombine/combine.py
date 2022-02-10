# Ball class has properties for x, y and radius
# and constants for the margin of error on the x/y axis, and for the radius
class Ball:
    margin_xy = 10 # margin of error on x,y
    margin_r = 10 # margin of error on radius

    def __init__(self,x,y,radius):
        self.x = x
        self.y = y
        self.radius = radius

# util function checks if number is between lower and upper values
def within_range(num, lower, upper):
    return num < upper and num > lower

# takes a two lists of balls and returns a single list of balls that seem to match each other within the margin
# of error defined in the Ball class
def get_valid_balls(balls1, balls2):
    balls1 = balls1.copy()
    balls2 = balls2.copy()
    
    valid_balls = []
    nocheck1 = []
    nocheck2 = []
    
    for ball1 in balls1:
        if ball1 in nocheck1: continue
        for ball2 in balls2:
            if ball2 in nocheck2: continue
            # only add a ball to the list if it matches with a ball from a different method, within a margin of error defined in Ball
            if within_range(ball1.x, ball2.x - Ball.margin_xy, ball2.x + Ball.margin_xy) and within_range(ball1.y, ball2.y - Ball.margin_xy, ball2.y + Ball.margin_xy) and within_range(ball1.radius, ball2.radius - Ball.margin_r, ball2.radius + Ball.margin_r):
                valid_balls.append(Ball(int((ball1.x + ball2.x) / 2), int((ball1.y + ball2.y) / 2), int((ball1.radius + ball2.radius) / 2)))
                
                # don't check these balls again, they've already been matched
                nocheck1.append(ball1)
                nocheck2.append(ball2)
    
    return valid_balls;
