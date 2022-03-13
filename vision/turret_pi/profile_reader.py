import pstats
p = pstats.Stats('profile.txt')
p.print_stats(25)
