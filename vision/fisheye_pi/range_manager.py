import json
import os

data = os.path.join(os.path.dirname(os.path.realpath(__file__)), "Data")

class Range:
    def __init__(self, id: str, lower: tuple[3], upper: tuple[3]):
        self.id = id
        self.lower = lower
        self.upper = upper


    def update(self, lower: tuple[3], upper: tuple[3]):
        self.lower = lower
        self.upper = upper


def range_to_object(range: Range) -> dict:
    return {
            "id": range.id,
            "lower": range.lower,
            "upper": range.upper
        }


def object_to_range(d: dict) -> Range:
    return Range(d["id"], d["lower"], d["upper"])


def store_range_object(range: Range):
    outfile = open(os.path.join(data, "Ranges", f"{range.id}.json"), "w")
    json.dump(range_to_object(range), outfile)
    # with open(os.path.join("Data", "Ranges", f"{range.id}.json"), "w") as outfile:
    #     outfile.write(range_to_object(range))


def get_range(id: str) -> Range:
    try:
        return object_to_range(json.load(open(os.path.join(data, "Ranges", f"{id}.json"))))
    except:
        return None
