# ---------------------------------------------------------------------------
#           Name: sv_instagib.py
#           Description: instagib server trigger
# ---------------------------------------------------------------------------

# Savage API
import core
import server

# External modules
import sv_defs
import sv_utils
import random
import time
import threading
import sv_custom_utils

# Global vars
global game_mod

are_flags_found = False
run_once_flag = False
type_list = ["CLIENT", "WORKER", "NPC", "MINE", "BASE", "OUTPOST", "BUILDING", "OTHER"]
teleport_locations = []
lock = threading.Lock()

# A queue of dead players that should be revived
dead_queue = set()

FRAG_LIMIT = 3
INSTAGIB_MOD = "INSTAGIB"
available_game_states = (1, 2, 3)

# human_stronghold was excluded from the teleport locations
possible_teleport_locations = ("spawnflag")


# Is called during every server frame
def check():
    try:
        # Run-once
        run_once()

        # Runs only for INSTAGIB_MOD
        if game_mod != INSTAGIB_MOD:
            return 0

        # If not game setup, warmup or normal
        if server.GetGameInfo(GAME_STATE) not in available_game_states:
            return 0

        for index in xrange(0, sv_defs.objectList_Last):
            if sv_defs.objectList_Active[index]:
                object_type = str(type_list[sv_defs.objectList_Type[index]])
                object_health = int(sv_defs.objectList_Health[index])
                if object_type == "CLIENT" and object_health == 0:
                    teleport_and_revive(str(index))
        get_team_stats()
        update_clients_vars()
        is_time_to_finish()
    except:
        sv_custom_utils.simple_exception_info()
    return 0


def run_once():
    global run_once_flag
    if not run_once_flag:
        run_once_flag = True
        print("________SV_INSTAGIB_______")
        check_mod()
        find_teleport_locations()
        reset_clients_vars()


# Checks the current mod of the game
def check_mod():
    global game_mod
    game_mod = core.CvarGetString('sv_map_gametype')
    print("[!]   MOD: %s" % game_mod)


# Gets an array of the team frags ([T1_FRAGS, T2_FRAGS]).
# There could be the same server variable and this duplicate logic is useless
def get_team_stats():
    objects_team_1 = []
    objects_team_2 = []
    global team_frags
    team_frags = [0, 0]
    for index in range(0, MAX_CLIENTS):
        if sv_defs.clientList_Team[index] == 1:
            objects_team_1.append(str(index))
        if sv_defs.clientList_Team[index] == 2:
            objects_team_2.append(str(index))
    for idx_1 in objects_team_1:
        team_frags[0] += int(server.GetClientInfo(int(idx_1), STAT_KILLS))
    for idx_2 in objects_team_2:
        team_frags[1] += int(server.GetClientInfo(int(idx_2), STAT_KILLS))
    return team_frags


# Global variables (gs_transmit1-9) that are being transferred to the clients:
def update_clients_vars():
    # gs_transmit1 = TEAM_1 Frags (RED)
    core.CommandExec("set gs_transmit1 %s" % team_frags[0])
    # gs_transmit2 = TEAM_2 Frags (BLUE)
    core.CommandExec("set gs_transmit2 %s" % team_frags[1])
    # gs_transmit3 = FRAG_LIMIT
    core.CommandExec("set gs_transmit3 %s" % FRAG_LIMIT)


def reset_clients_vars():
    for idx in range(1, 10):
        core.CommandExec("set gs_transmit%s 0" % idx)


def teleport_and_revive(guid):
    guid = int(guid)
    global dead_queue
    if guid not in dead_queue:
        dead_queue.add(guid)
        createThread('import sv_instagib; sv_instagib.execute_waiting_and_reviving(%s)' % guid)


def execute_waiting_and_reviving(guid):
    guid = int(guid)
    # Sleeping N seconds before any further actions. Is done to prevent interrupting of the death animation and effects
    time.sleep(1)

    # If game state is setup, warmup or normal
    try:
        if server.GetGameInfo(GAME_STATE) in available_game_states:
            with lock:
                global dead_queue
                core.CommandExec('revive %s' % guid)
                Point3 = get_random_spawn_location()
                print("Teleporting id: %s [%s, %s]" % (guid, Point3[0], Point3[1]))
                server.GameScript(guid, '!teleport target coords %s %s' % (Point3[0], Point3[1]))
                server.GameScript(guid, '!heal target 500')
                # !remove target slot (slots: 0,1,2,3,4)
                server.GameScript(guid, '!remove target 1')
                # !give target human_coilrifle ammo slot (slots: 0,1,2,3,4)
                server.GameScript(guid, '!give target human_coilrifle 1000 1')
                if guid in dead_queue:
                    dead_queue.remove(guid)
                time.sleep(0.5)
    except:
        sv_custom_utils.simple_exception_info()


# Finds all possible teleport location from the 'teleport_locations' list.
# Should be run-once at start
def find_teleport_locations():
    global are_flags_found
    global teleport_locations
    if not are_flags_found:
        for index in xrange(0, sv_defs.objectList_Last):
            if sv_defs.objectList_Active[index]:
                object_name = str(sv_defs.objectList_Name[index])
                if object_name in possible_teleport_locations:
                    teleport_locations.append(sv_utils.get_point3(index))
        print("Teleport locations[%s]:" % len(teleport_locations))
        for t in teleport_locations:
            print(" - %s" % t)
        are_flags_found = True


def get_random_spawn_location():
    return random.choice(teleport_locations)


# Checks conditions (such as time and frag limits) to end the current game
def is_time_to_finish():
    # Normal play mode: 3
    if server.GetGameInfo(GAME_STATE) == 3:
        # Dirty hack to count max_time without 1 second (was done to prevent overtime)
        max_time = int(core.CvarGetValue('gs_game_status_end')) - 1000
        current_time = int(core.CvarGetValue('gs_game_time'))
        team_frags = get_team_stats()
        if current_time >= max_time:
            core.CommandExec('endgame %s' % get_team_winner(team_frags))
            return
        if team_frags[0] == FRAG_LIMIT and team_frags[1] == FRAG_LIMIT:
            core.CommandExec('endgame 0')
            return
        if team_frags[0] == FRAG_LIMIT:
            core.CommandExec('endgame 1')
            return
        if team_frags[1] == FRAG_LIMIT:
            core.CommandExec('endgame 2')


def get_team_winner(team_frags):
    if team_frags[0] > team_frags[1]:
        return 1
    elif team_frags[0] < team_frags[1]:
        return 2
    return 0


# Is called when check() returns 1
# Is not used in the current script
def execute():
    pass