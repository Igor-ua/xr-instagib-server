# ---------------------------------------------------------------------------
#           Name: sv_custom_utils.py
#           Description: additional utils
# ---------------------------------------------------------------------------


# Savage API
import core
import server

# External modules
import sv_defs
import sys
import traceback


def custom_exception_info():
    try:
        traceback_template = '''Traceback:
          File "%(filename)s", line %(line_no)s, in %(name)s
        %(type)s: %(message)s\n'''

        exc_type, exc_value, exc_traceback = sys.exc_info()
        traceback_details = {
            'filename': exc_traceback.tb_frame.f_code.co_filename,
            'line_no': exc_traceback.tb_lineno,
            'name': exc_traceback.tb_frame.f_code.co_name,
            'type': exc_type.__name__,
            'message': exc_value.message,  # or see traceback._some_str()
        }
        del (exc_type, exc_value, exc_traceback)
        print
        print traceback_template % traceback_details
        print
    except:
        pass


def full_exception_info():
    print
    print traceback.format_exc()
    print


def simple_exception_info():
    try:
        exc_type, exc_value, exc_traceback = sys.exc_info()
        exception_data = traceback.format_exc().splitlines()

        root_info = str(exception_data[-3]).split(",")
        root_info.append(exception_data[-2])
        for idx in range(0, len(root_info) - 1):
            root_info[idx] = root_info[idx].strip()
        root_info[0] = root_info[0].split("/")[-1].replace("\"", "")
        root_info[3] = root_info[3].strip()

        top_file_name = str(exc_traceback.tb_frame.f_code.co_filename).split("/")[-1]
        top_method_name = str(exc_traceback.tb_frame.f_code.co_name)
        del (exc_type, exc_value, exc_traceback)

        print
        print "Traceback:"
        print "  Top:  file: [%s], method: %s()" % (top_file_name, top_method_name)
        print "  Root: file: [%s], %s, cause: %s [%s]" % (root_info[0], root_info[1], root_info[2], root_info[3])
        print "  " + exception_data[-1]
        print
    except:
        print
        print "Error in simple_exception_info()"
        print traceback.format_exc()
        print


def show_object(index):
    type_list = ["CLIENT", "WORKER", "NPC", "MINE", "BASE", "OUTPOST", "BUILDING", "OTHER"]

    res = "id: " + str(index) + ", "
    res += "type: " + str(type_list[sv_defs.objectList_Type[index]]) + ", "
    res += "name: " + str(sv_defs.objectList_Name[index]) + ", "
    res += "team: " + str(sv_defs.objectList_Team[index]) + ", "
    res += "health: " + str(sv_defs.objectList_Health[index]) + ", "
    res += "construct: " + str(sv_defs.objectList_Construct[index]) + ", "
    res += "active: " + str(sv_defs.objectList_Active[index])
    print res


def check_objects():
    print("[Map Objects]")
    # global objects
    # Get the initial list of all objects
    # Print the information about all "active" objects
    for index in xrange(0, sv_defs.objectList_Last):
        if sv_defs.objectList_Active[index]:
            show_object(index)


# This function copies the information of all server objects in a list
# The main reason for this is to have a real *copy* of all acquired property-lists, not just pointers to them
def make_list(pair):
    res = []
    my_list = list(pair)
    for i in xrange(len(my_list) - 1):
        tmp = []
        for j in xrange(sv_defs.objectList_Last):
            tmp.append(my_list[i][j])
        # There is some magic that the size of every property-list contains 1024 elements
        # However, the positions of any non-existent objects are not accessible in this list,
        # so we explicitly fill the corresponding positions with None-s
        for j in xrange(sv_defs.objectList_Last, 1024):
            tmp.append(None)
        res.append(tmp)
    # The last element of tuple is an integer "sv_defs.objectList_Last", so we process it separately
    res.append(my_list[7])
    return res