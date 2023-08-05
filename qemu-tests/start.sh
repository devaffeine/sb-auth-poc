#cloud-localds imgs/user-data.img user-data
qemu-system-x86_64 -enable-kvm -m 4G -smp 2 -nographic \
     -drive file=imgs/vms/ubuntu.qcow2,format=qcow2 \
     -drive file=imgs/vms/user-data.img,format=raw
