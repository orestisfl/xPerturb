FROM orestisfl/env:latest
RUN apt-get install -qqy python-tqdm python-termcolor ipython time zip
RUN cd / \
    && git clone --depth 1 https://github.com/KTH/slumps \
    && cd /slumps/vulnerable_programs/deadpool/nosuchcon_2013_whitebox_noenc/bitcode \
    # Fetch generator & create needed file
    && curl https://raw.githubusercontent.com/SideChannelMarvels/Deadpool/master/wbs_aes_nsc2013_variants/target/nosuchcon_2013_whitebox_noenc_generator.c | gcc -x c -o nosuchcon_2013_whitebox_noenc_generator - \
    && ./nosuchcon_2013_whitebox_noenc_generator \
    && python3 verify.py
