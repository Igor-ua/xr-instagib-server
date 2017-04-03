# ---------------------------------------------------------------------------
#           Name: sv_update.py
#    Description: Updated that works in the end of the game.
#                 Should be used for Linux servers.
# ---------------------------------------------------------------------------

# Savage API
import core
import server

# Python API
import urllib
import sys

# Variables ---------------------
run_checkers = True
run_once = True
local_version = 0
remote_version = 0
remote_url = 'http://www.newerth.com/autoupdater/production/linux/server/autoupdater/version.txt'
# Base init location is: server_dir/game/
local_file_path = '../autoupdater/localversion/version.txt'


# -------------------------------
def check():
    global run_checkers
    global run_once
    # If the game has ended - check remote and local versions of the server
    if server.GetGameInfo(GAME_STATE) == 4 and run_checkers:
        run_checkers = False
        core.CvarGetValue('svr_update_available', 0)
        check_local_version()
        check_remote_version()
        compare_versions()
    # If the game is about to load/restart a map server does own update
    if server.GetGameInfo(GAME_STATE) > 4 and run_once:
        run_once = False
        if int(core.CvarGetValue('svr_update_available')):
            return 1
    return 0


def check_local_version():
    global local_version
    global remote_version
    try:
        with open(local_file_path, 'r') as content_file:
            local_version = int(content_file.read())
            # Remove version should be the same as the local version before you check remote version
            remote_version = local_version
    except:
        print sys.exc_info()


def check_remote_version():
    global remote_version
    try:
        remote_version = int(urllib.urlopen(remote_url).read())
    except:
        print sys.exc_info()


def compare_versions():
    core.ConsolePrint("Checking version: local is: %s, remote is: %s\n" % (local_version, remote_version))
    if remote_version > local_version:
        core.CvarSetValue('svr_update_available', 1)
        # Notify all clients that update will take place
        server.Notify(-1, "^779(XR AutoUpdater) ^wThe server will restart to apply XR's latest updates.")
        server.Notify(-1, "^779(XR AutoUpdater) ^wPlease Wait! You will automatically reconnect if you have the latest version of XR.")
    elif local_version == remote_version:
        core.CvarSetValue('svr_update_available', 0)
        core.ConsolePrint("Local and remote versions are the same.")


# -------------------------------
def execute():
    core.ConsolePrint("Applying an update: from %s to %s" % (local_version, remote_version))
    # Turn off the server to apply an update:
    core.CommandExec('quit')
